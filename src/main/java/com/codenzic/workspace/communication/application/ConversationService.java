package com.codenzic.workspace.communication.application;

import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.common.api.PageResponse;
import com.codenzic.workspace.communication.domain.Conversation;
import com.codenzic.workspace.communication.domain.ConversationParticipant;
import com.codenzic.workspace.communication.domain.Message;
import com.codenzic.workspace.communication.infrastructure.ConversationParticipantRepository;
import com.codenzic.workspace.communication.infrastructure.ConversationRepository;
import com.codenzic.workspace.communication.infrastructure.MessageRepository;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.ConversationRequest;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.ConversationResponse;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.MessageRequest;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.MessageResponse;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.identity.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversations;
    private final ConversationParticipantRepository participants;
    private final MessageRepository messages;
    private final UserRepository users;

    @Transactional
    public ConversationResponse create(ConversationRequest request) {
        UUID organizationId = organizationId();
        Set<UUID> participantIds = new HashSet<>(request.participantIds());
        participantIds.add(CurrentUser.id());
        List<User> foundUsers = users.findAllById(participantIds);
        if (foundUsers.size() != participantIds.size()
                || foundUsers.stream().anyMatch(user -> !organizationId.equals(user.getOrganizationId()))) {
            throw error("PARTICIPANT_SCOPE", "All participants must belong to your organization", HttpStatus.FORBIDDEN);
        }
        Conversation conversation = conversations.save(new Conversation(organizationId, request.title(), CurrentUser.id()));
        participantIds.forEach(userId -> participants.save(new ConversationParticipant(conversation.getId(), userId)));
        return to(conversation, participantIds, null);
    }

    @Transactional(readOnly = true)
    public PageResponse<ConversationResponse> list(Pageable pageable) {
        Page<ConversationResponse> page = conversations.findVisibleToUser(organizationId(), CurrentUser.id(), pageable)
                .map(conversation -> to(conversation, participantIds(conversation.getId()),
                        readAt(conversation.getId())));
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public ConversationResponse get(UUID id) {
        Conversation conversation = find(id);
        requireParticipant(id);
        return to(conversation, participantIds(id), readAt(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> messages(UUID id, Pageable pageable) {
        find(id);
        requireParticipant(id);
        return PageResponse.from(messages.findAllByConversationIdOrderByCreatedAtAsc(id, pageable).map(this::to));
    }

    @Transactional
    public MessageResponse send(UUID id, MessageRequest request) {
        find(id);
        requireParticipant(id);
        return to(messages.save(new Message(id, CurrentUser.id(), request.body())));
    }

    @Transactional
    public ConversationResponse markRead(UUID id) {
        Conversation conversation = find(id);
        ConversationParticipant participant = participants.findByIdConversationIdAndIdUserId(id, CurrentUser.id())
                .orElseThrow(() -> error("CONVERSATION_FORBIDDEN", "You are not a participant", HttpStatus.FORBIDDEN));
        participant.markRead();
        participants.save(participant);
        return to(conversation, participantIds(id), participant.getLastReadAt());
    }

    private Conversation find(UUID id) {
        return conversations.findByIdAndOrganizationId(id, organizationId())
                .orElseThrow(() -> error("CONVERSATION_NOT_FOUND", "Conversation not found", HttpStatus.NOT_FOUND));
    }

    private void requireParticipant(UUID id) {
        if (!participants.existsByIdConversationIdAndIdUserId(id, CurrentUser.id())) {
            throw error("CONVERSATION_FORBIDDEN", "You are not a participant", HttpStatus.FORBIDDEN);
        }
    }

    private Set<UUID> participantIds(UUID id) {
        return participants.findAllByIdConversationId(id).stream()
                .map(participant -> participant.getId().getUserId())
                .collect(Collectors.toSet());
    }

    private java.time.Instant readAt(UUID conversationId) {
        return participants.findByIdConversationIdAndIdUserId(conversationId, CurrentUser.id())
                .map(ConversationParticipant::getLastReadAt).orElse(null);
    }

    private ConversationResponse to(Conversation conversation, Set<UUID> participantIds, java.time.Instant readAt) {
        return new ConversationResponse(conversation.getId(), conversation.getOrganizationId(), conversation.getTitle(),
                conversation.getCreatedBy(), participantIds, conversation.getCreatedAt(), readAt,
                messages.countUnread(conversation.getId(), CurrentUser.id(), readAt));
    }

    private MessageResponse to(Message message) {
        return new MessageResponse(message.getId(), message.getConversationId(), message.getSenderId(),
                message.getBody(), message.getCreatedAt());
    }

    private UUID organizationId() {
        return CurrentUser.requiredOrganizationId();
    }

    private BusinessException error(String code, String message, HttpStatus status) {
        return new BusinessException(code, message, status);
    }
}
