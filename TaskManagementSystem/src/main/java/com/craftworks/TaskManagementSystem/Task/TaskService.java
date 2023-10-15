package com.craftworks.TaskManagementSystem.Task;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskDTOMapper taskDTOMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskDTOMapper taskDTOMapper) {
        this.taskRepository = taskRepository;
        this.taskDTOMapper = taskDTOMapper;
    }

    public List<TaskDTO> getTasks() {
        return taskRepository.findAll().stream().map(taskDTOMapper).collect(Collectors.toList());
    }

    public TaskDTO getTask(Long taskId) {
        return taskRepository.findById(taskId).map(taskDTOMapper).orElseThrow(() ->
                new ResourceNotFoundException("Task with id " + taskId + " does not exist"));
    }

    public Long addNewTask(Task task) {
        taskRepository.findTaskByTitle(task.getTitle()).ifPresent(
                t -> {
                    throw new BadArgumentException("Task with title " + task.getTitle() + " already exist");
                }
        );
        return taskRepository.save(task).getId();
    }

    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task with id " + taskId + " does not exist");
        }
        taskRepository.deleteById(taskId);
    }


    @Transactional(rollbackOn = Exception.class)
    public void updateTask(Long taskId, LocalDate dueDate, String title, String description, Task.PriorityLevel priority, Task.Status status) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new ResourceNotFoundException("Task with id " + taskId + " does not exist"));

        if (dueDate != null) {
            try {
                task.setDueDate(dueDate);
            } catch (IllegalArgumentException e) {
                throw new BadArgumentException(e.getMessage());
            }
        }
        if (title != null) {
            taskRepository.findTaskByTitle(title).ifPresent(
                    t -> {
                        throw new BadArgumentException("Task with title " + title + " already exist");
                    });
            try {
                task.setTitle(title);
            } catch (IllegalArgumentException e) {
                throw new BadArgumentException(e.getMessage());
            }
        }
        if (description != null) {
            task.setDescription(description);
        }
        if (priority != null) {
            task.setPriority(priority);
        }
        if (status != null) {
            task.setStatus(status);
        }
    }
}
