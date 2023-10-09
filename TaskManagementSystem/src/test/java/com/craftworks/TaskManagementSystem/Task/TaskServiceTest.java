package com.craftworks.TaskManagementSystem.Task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task1, task2;

    @BeforeEach
    public void setup(){
        task1 = new Task(
                "First Task",
                "Task 1 is a test task",
                Task.PriorityLevel.LOW,
                LocalDate.of(2023, Month.DECEMBER, 1));
        task2 = new Task(
                "Second Task",
                "Task 2 is a test task",
                Task.PriorityLevel.LOW,
                LocalDate.of(2023, Month.DECEMBER, 2));
    }

    @Test
    void givenTasks_getTasks() {
        given(taskRepository.findAll()).willReturn(Arrays.asList(task1, task2));
        List<Task> tasks = taskService.getTasks();
        assertEquals(tasks.size(), 2);
    }

    @Test
    void givenTasks_getTask_second() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        Task task = taskService.getTask(2L);
        assertEquals(task2.getCreatedAt(), task.getCreatedAt());
    }

    @Test
    void givenTasks_getTask_third() {
        given(taskRepository.findById(3L)).willReturn(Optional.empty());

        Assertions.assertThrows(IllegalStateException.class, () -> {
            taskService.getTask(3L);
        });
    }

    @Test
    void givenTasks_addTask_new() {
        Task task3 = new Task(
                "Third Task",
                "Task 3 is a test task",
                Task.PriorityLevel.LOW,
                LocalDate.of(2023, Month.DECEMBER, 2));
        task3.setStatus(Task.Status.IN_PROGRESS);
        task3.setCreatedAt(LocalDate.of(2023, Month.NOVEMBER, 27));

        given(taskRepository.findTaskByTitle(task3.getTitle())).willReturn(Optional.empty());
        taskService.addNewTask(task3);

        assertEquals(Task.Status.NOT_STARTED, task3.getStatus());
        assertEquals(LocalDate.now(), task3.getCreatedAt());
    }

    @Test
    void givenTasks_addTask_existing() {
        given(taskRepository.findTaskByTitle(task2.getTitle())).willReturn(Optional.of(task2));
        Assertions.assertThrows(IllegalStateException.class, () -> {
            taskService.addNewTask(task2);
        });
    }

    @Test
    void givenTasks_deleteTask_NonExisting() {
        given(taskRepository.existsById(3L)).willReturn(false);
        Assertions.assertThrows(IllegalStateException.class, () -> {
            taskService.deleteTask(3L);
        });
    }

    @Test
    void givenTasks_updateTask_nonExisting() {
        given(taskRepository.findById(3L)).willReturn(Optional.empty());
        Assertions.assertThrows(IllegalStateException.class, () -> {
            taskService.updateTask(3L, null, null, null, null, null);
        });

    }

    @Test
    void givenTasks_updateTask_empty() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, null, null, null, null);
        assertNull(task2.getUpdatedAt());
    }

    @Test
    void givenTasks_updateTask_dueDate() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, LocalDate.now(), null, null, null, null);
        assertTrue(LocalDate.now().isEqual(task2.getDueDate()));
        assertTrue(LocalDate.now().isEqual(task2.getUpdatedAt()));
    }

    @Test
    void givenTasks_updateTask_dueDateFalse() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        Assertions.assertThrows(IllegalStateException.class, () -> {
            taskService.updateTask(2L, LocalDate.of(2022, Month.DECEMBER, 1), null,
                    null, null, null);
        });
        assertNull(task2.getUpdatedAt());
    }

    @Test
    void givenTasks_updateTask_dueDateSameAsCreation() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, task2.getCreatedAt(), null, null, null, null);
        assertTrue(task2.getCreatedAt().isEqual(task2.getDueDate()));
        assertTrue(LocalDate.now().isEqual(task2.getUpdatedAt()));
    }

    @Test
    void givenTasks_updateTask_title() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, "Second task edited title", null, null, null);
        assertEquals("Second task edited title", task2.getTitle());
        assertTrue(LocalDate.now().isEqual(task2.getUpdatedAt()));
    }

    @Test
    void givenTasks_updateTask_titleEmpty() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        Assertions.assertThrows(IllegalStateException.class, () -> {
                    taskService.updateTask(2L, null, "", null, null, null);
                });
        assertNull(task2.getUpdatedAt());
    }

    @Test
    void givenTasks_updateTask_description() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, null, "New description", null, null);
        assertEquals("New description", task2.getDescription());
        assertTrue(LocalDate.now().isEqual(task2.getUpdatedAt()));
    }

    @Test
    void givenTasks_updateTask_priority() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, null, null, Task.PriorityLevel.MEDIUM, null);
        assertEquals(task2.getPriority(),Task.PriorityLevel.MEDIUM);
        assertTrue(LocalDate.now().isEqual(task2.getUpdatedAt()));
    }

    @Test
    void givenTasks_updateTask_statusInProgress() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, null, null, null, Task.Status.IN_PROGRESS);
        assertEquals(task2.getStatus(),Task.Status.IN_PROGRESS);
        assertTrue(LocalDate.now().isEqual(task2.getUpdatedAt()));
        assertNull(task2.getResolvedAt());
    }

    @Test
    void givenTasks_updateTask_statusCompleted() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, null, null, null, Task.Status.COMPLETED);
        assertEquals(task2.getStatus(),Task.Status.COMPLETED);
        assertTrue(LocalDate.now().isEqual(task2.getUpdatedAt()));
        assertTrue(LocalDate.now().isEqual(task2.getResolvedAt()));
    }

    @Test
    void givenTasks_updateTask_statusPriority() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, null, null, Task.PriorityLevel.HIGH, Task.Status.COMPLETED);
        assertEquals(task2.getStatus(),Task.Status.COMPLETED);
        assertEquals(task2.getPriority(),Task.PriorityLevel.HIGH);
        assertTrue(LocalDate.now().isEqual(task2.getUpdatedAt()));
        assertTrue(LocalDate.now().isEqual(task2.getResolvedAt()));
    }
}