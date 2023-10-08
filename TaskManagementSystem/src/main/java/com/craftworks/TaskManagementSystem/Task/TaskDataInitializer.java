package com.craftworks.TaskManagementSystem.Task;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
public class TaskDataInitializer {

    @Bean
    CommandLineRunner commandLineRunner(TaskRepository repository) {
        return args -> {
            Task task1 = new Task(
                    "First Task",
                    "Task 1 is a test task",
                    Task.PriorityLevel.LOW,
                    LocalDate.of(2023, Month.DECEMBER, 1));
            Task task2 = new Task(
                    "Second Task",
                    "Task 2 is a test task",
                    Task.PriorityLevel.LOW,
                    LocalDate.of(2023, Month.DECEMBER, 2));

            repository.saveAll(List.of(task1, task2));
        };
    }
}
