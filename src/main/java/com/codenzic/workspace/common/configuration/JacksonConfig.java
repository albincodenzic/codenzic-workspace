package com.codenzic.workspace.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Registers module for Java 8 Date/Time types (Instant, LocalDateTime, etc.)
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}