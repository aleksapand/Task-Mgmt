package com.craftworks.TaskManagementSystem.Task;

import java.time.LocalDate;

public record TaskDTO(
        Long id,
        LocalDate createdAt,
        LocalDate updatedAt,
        LocalDate dueDate,
        LocalDate resolvedAt,
        String title,
        String description,
        Task.PriorityLevel priority,
        Task.Status status) {
}
