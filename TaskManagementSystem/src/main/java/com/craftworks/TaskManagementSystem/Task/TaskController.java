package com.craftworks.TaskManagementSystem.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path="api/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getTasks() {
        // TODO: Take care of ordered list, possibly include "order by"
        return taskService.getTasks();
    }

    @GetMapping(path = "{taskId}")
    public Task getTask(@PathVariable("taskId") Long taskId)
    {
        return taskService.getTask(taskId);
    }

    @PostMapping
    public void addNewTask(@RequestBody Task task) {
        taskService.addNewTask(task);
    }

    @DeleteMapping(path = "{taskId}")
    public void deleteTask(@PathVariable("taskId") Long taskId) {
        taskService.deleteTask(taskId);
    }

    @PutMapping(path = "{taskId}")
    public void updateTask(
            @PathVariable("taskId") Long taskId,
            @RequestParam(required = false) LocalDate dueDate,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Task.PriorityLevel priority,
            @RequestParam(required = false) Task.Status status) {

        taskService.updateTask(taskId, dueDate, title, description, priority, status);
    }
}
