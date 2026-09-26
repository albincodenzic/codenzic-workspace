package com.codenzic.workspace.task.domain;
import jakarta.persistence.*; import lombok.*; import java.util.*; import java.time.LocalDate;
@Entity @Table(name="tasks") @Getter @NoArgsConstructor
public class Task { @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; UUID organizationId; UUID projectId; UUID teamId; UUID assigneeId; @Column(nullable=false,length=250) String title; @Column(length=2000) String description; @Column(nullable=false,length=30) String status="TODO"; @Column(nullable=false,length=30) String priority="MEDIUM"; LocalDate dueDate;
 public Task(UUID organizationId,UUID projectId,UUID teamId,UUID assigneeId,String title,String description,String status,String priority,LocalDate dueDate) { this.organizationId=organizationId;this.projectId=projectId;this.teamId=teamId;this.assigneeId=assigneeId;this.title=title;this.description=description;this.status=status;this.priority=priority;this.dueDate=dueDate; }
 public void update(UUID projectId,UUID teamId,UUID assigneeId,String title,String description,String status,String priority,LocalDate dueDate) { this.projectId=projectId;this.teamId=teamId;this.assigneeId=assigneeId;this.title=title;this.description=description;this.status=status;this.priority=priority;this.dueDate=dueDate; }
 public void changeStatus(String status) { this.status=status; }
 public void changePriority(String priority) { this.priority=priority; }
 public void assign(UUID assigneeId) { this.assigneeId=assigneeId; }
}