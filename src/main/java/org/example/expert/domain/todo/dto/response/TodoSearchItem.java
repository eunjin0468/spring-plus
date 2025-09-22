package org.example.expert.domain.todo.dto.response;

public record TodoSearchItem(
        Long todoId,
        String title,
        long managerCount,
        long commentCount,
        java.time.LocalDateTime createdAt
)
{}