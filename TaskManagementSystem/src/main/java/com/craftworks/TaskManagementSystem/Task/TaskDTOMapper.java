package com.craftworks.TaskManagementSystem.Task;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TaskDTOMapper implements Function<Task, TaskDTO> {

    @Override
    public TaskDTO apply(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getDueDate(),
                task.getResolvedAt(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus()
        );
    }
}
