package com.craftworks.TaskManagementSystem.Task;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    public Task getTask(Long taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if(task.isEmpty()) {
            throw new IllegalStateException("Task with id " + taskId + " does not exist");
        }
        return task.get();
    }

    public void addNewTask(Task task) {
        // TODO: Check for title duplicate?
        task.setCreatedAt(LocalDate.now());
        task.setStatus(Task.Status.NOT_STARTED);
        taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        if(!taskRepository.existsById(taskId)) {
            //TODO: Unify repeated error messages
            throw new IllegalStateException("Task with id " + taskId + " does not exist");
        }
        taskRepository.deleteById(taskId);
    }


    @Transactional(rollbackOn = Exception.class)
    public void updateTask(Long taskId, LocalDate dueDate, String title, String description, Task.PriorityLevel priority, Task.Status status) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            throw new IllegalStateException("Task with id " + taskId + " does not exist");
        }
        Task task = optionalTask.get();
        boolean updated = false;

        if(dueDate != null) {
            if(dueDate.isAfter(task.getCreatedAt()) || dueDate.isEqual(task.getCreatedAt())) {
                task.setDueDate(dueDate);
                updated = true;
            } else {
                throw new IllegalStateException("Invalid due date. Due date before creation time.");
            }
        }
        if(title != null) {
            if(!title.isEmpty()) {
                task.setTitle(title);
                updated = true;
            } else {
                throw new IllegalStateException("Invalid due date. Due date before creation time.");
            }
        }
        if(description != null) {
            task.setDescription(description);
            updated = true;
        }
        if(priority != null) {
            task.setPriority(priority);
            updated = true;
        }
        if(status != null) {
            task.setStatus(status);
            if (status == Task.Status.COMPLETED) {
                task.setResolvedAt(LocalDate.now());
            }
            updated = true;
        }
        if (updated) {
            task.setUpdatedAt(LocalDate.now());
        }
    }
}
