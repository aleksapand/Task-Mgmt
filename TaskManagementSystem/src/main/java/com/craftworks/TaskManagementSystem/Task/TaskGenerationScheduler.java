package com.craftworks.TaskManagementSystem.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.Random;

@Component
@EnableScheduling
@Profile("dev | prod")
public class TaskGenerationScheduler {
    private final TaskService taskService;
    private Long taskCounter;
    private static final Random RANDOM = new Random();
    private final Integer priorityLen = Task.PriorityLevel.values().length;

    @Autowired
    public TaskGenerationScheduler(TaskService taskService) {
        this.taskService = taskService;
        this.taskCounter = 0L;
    }

    @Scheduled(fixedDelayString = "${scheduler.delay}")
    public void generateNewTask() {
        this.taskCounter += 1;
        Task task = new Task(
                "Task " + taskCounter,
                "Task " + taskCounter + " is a test task",
                Task.PriorityLevel.values()[RANDOM.nextInt(priorityLen)],
                LocalDate.of(2023, Month.DECEMBER, 1));

        this.taskService.addNewTask(task);
    }
}
