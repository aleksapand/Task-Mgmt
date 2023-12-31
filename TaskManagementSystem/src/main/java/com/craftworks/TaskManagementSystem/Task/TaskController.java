package com.craftworks.TaskManagementSystem.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "api/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDTO> getTasks() {
        return taskService.getTasks();
    }

    @GetMapping(path = "{taskId}")
    public TaskDTO getTask(@PathVariable("taskId") Long taskId) {
        return taskService.getTask(taskId);
    }

    @PostMapping
    public Long addNewTask(@RequestBody Task task) {
        return taskService.addNewTask(task);
    }

    @DeleteMapping(path = "{taskId}")
    public void deleteTask(@PathVariable("taskId") Long taskId) {
        taskService.deleteTask(taskId);
    }

    @PutMapping(path = "{taskId}")
    public void updateTask(
            @PathVariable("taskId") Long taskId,
            @RequestParam(required = false) LocalDateTime dueDate,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Task.PriorityLevel priority,
            @RequestParam(required = false) Task.Status status) {

        taskService.updateTask(taskId, dueDate, title, description, priority, status);
    }
}
