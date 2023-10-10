package com.craftworks.TaskManagementSystem.Task;

import jakarta.transaction.Transactional;
import org.hibernate.ResourceClosedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.EnumUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
        Optional<TaskDTO> task = taskRepository.findById(taskId).map(taskDTOMapper);
        if(task.isEmpty()) {
            throw new ResourceNotFoundException("Task with id " + taskId + " does not exist");
        }
        return task.get();
    }

    public void addNewTask(Task task) {
        Optional<Task> taskOptional = taskRepository.findTaskByTitle(task.getTitle());
        if(taskOptional.isPresent()) {
            throw new BadArgumentException("Task with title " + task.getTitle() + " already exist");
        }
        task.setCreatedAt(LocalDate.now());
        task.setStatus(Task.Status.NOT_STARTED);
        taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        if(!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task with id " + taskId + " does not exist");
        }
        taskRepository.deleteById(taskId);
    }


    @Transactional(rollbackOn = Exception.class)
    public void updateTask(Long taskId, LocalDate dueDate, String title, String description, Task.PriorityLevel priority, Task.Status status) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            throw new ResourceNotFoundException("Task with id " + taskId + " does not exist");
        }
        Task task = optionalTask.get();
        boolean updated = false;

        if(dueDate != null) {
            try {
                task.setDueDate(dueDate);
            } catch (IllegalArgumentException e) {
                throw new BadArgumentException(e.getMessage());
            }
            updated = true;
        }
        if(title != null) {
            Optional<Task> taskOptional = taskRepository.findTaskByTitle(title);
            if(taskOptional.isPresent()) {
                throw new BadArgumentException("Task with title " + task.getTitle() + " already exist");
            }
            try {
                task.setTitle(title);
            }
            catch (IllegalArgumentException e) {
                throw new BadArgumentException(e.getMessage());
            }
            updated = true;
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
            updated = true;
        }
        if (updated) {
            task.setUpdatedAt(LocalDate.now());
        }
    }
}
