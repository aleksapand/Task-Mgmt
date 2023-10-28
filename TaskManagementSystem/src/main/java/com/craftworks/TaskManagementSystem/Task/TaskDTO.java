package com.craftworks.TaskManagementSystem.Task;

import java.time.LocalDateTime;

public record TaskDTO(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime dueDate,
        LocalDateTime resolvedAt,
        String title,
        String description,
        Task.PriorityLevel priority,
        Task.Status status) {
}
