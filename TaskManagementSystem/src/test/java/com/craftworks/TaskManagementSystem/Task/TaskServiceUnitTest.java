package com.craftworks.TaskManagementSystem.Task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TaskServiceUnitTest {

    @Mock
    private TaskRepository taskRepository;

    private Task task1, task2;

    private final LocalDateTime dueDate = LocalDateTime.now().plusMonths(1).truncatedTo(ChronoUnit.MINUTES);

    private final TaskDTOMapper taskDTOMapper = new TaskDTOMapper();

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void setup() {
        this.taskService = new TaskService(taskRepository, taskDTOMapper);
        task1 = new Task("First Task", "Task 1 is a test task", Task.PriorityLevel.LOW, dueDate);
        task2 = new Task("Second Task", "Task 2 is a test task", Task.PriorityLevel.LOW, dueDate.plusDays(1));
    }

    @Test
    void test_getTasks() {
        List<Task> tasks = Arrays.asList(task1, task2);
        given(taskRepository.findAll()).willReturn(tasks);

        List<TaskDTO> expected = tasks.stream().map(taskDTOMapper).toList();
        List<TaskDTO> outcome = taskService.getTasks();
        assertEquals(outcome.size(), expected.size());
        assertEquals(outcome, expected);
    }

    @Test
    void test_getTask_second() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        TaskDTO expected = taskDTOMapper.apply(task2);
        TaskDTO task = taskService.getTask(2L);
        assertEquals(expected, task);
    }

    @Test
    void test_getTask_NonExisting() {
        given(taskRepository.findById(3L)).willReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> taskService.getTask(3L));
    }

    @Test
    void test_addTask_new() {
        Task task3 = new Task("Third Task", "Task 3 is a test task", Task.PriorityLevel.LOW, dueDate);
        task3.setStatus(Task.Status.IN_PROGRESS);

        given(taskRepository.findTaskByTitle(task3.getTitle())).willReturn(Optional.empty());
        given(taskRepository.save(task3)).willReturn(task3);
        taskService.addNewTask(task3);

        assertEquals(Task.Status.IN_PROGRESS, task3.getStatus());
    }

    @Test
    void test_addTask_existing() {
        given(taskRepository.findTaskByTitle(task2.getTitle())).willReturn(Optional.of(task2));
        Assertions.assertThrows(BadArgumentException.class, () -> taskService.addNewTask(task2));
    }

    @Test
    void test_deleteTask_NonExisting() {
        given(taskRepository.existsById(3L)).willReturn(false);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(3L));
    }

    @Test
    void test_updateTask_nonExisting() {
        given(taskRepository.findById(3L)).willReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(3L, null, null, null, null, null));

    }

    @Test
    void test_updateTask_empty() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, null, null, null, null);
        assertNull(task2.getUpdatedAt());
    }

    @Test
    void test_updateTask_dueDate() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, dueDate.plusMonths(1), null, null, null, null);
        assertEquals(dueDate.plusMonths(1), task2.getDueDate());
    }

    @Test
    void test_updateTask_dueDateFalse() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        Assertions.assertThrows(BadArgumentException.class, () -> taskService.updateTask(2L, dueDate.minusMonths(3), null, null, null, null));
        assertNull(task2.getUpdatedAt());
    }

    @Test
    void test_updateTask_title() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, "Second task edited title", null, null, null);
        assertEquals("Second task edited title", task2.getTitle());
    }

    @Test
    void test_updateTask_titleEmpty() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        Assertions.assertThrows(BadArgumentException.class, () -> taskService.updateTask(2L, null, "", null, null, null));
        assertNull(task2.getUpdatedAt());
    }

    @Test
    void test_updateTask_description() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, null, "New description", null, null);
        assertEquals("New description", task2.getDescription());
    }

    @Test
    void test_updateTask_priority() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, null, null, Task.PriorityLevel.MEDIUM, null);
        assertEquals(task2.getPriority(), Task.PriorityLevel.MEDIUM);
    }

    @Test
    void test_updateTask_statusInProgress() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, null, null, null, Task.Status.IN_PROGRESS);
        assertEquals(task2.getStatus(), Task.Status.IN_PROGRESS);
        assertNull(task2.getResolvedAt());
    }

    @Test
    void test_updateTask_statusCompleted() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, null, null, null, Task.Status.COMPLETED);
        assertEquals(task2.getStatus(), Task.Status.COMPLETED);
        long diff = Duration.between(LocalDateTime.now(), task2.getResolvedAt()).toMinutes();
        assertTrue(diff <= 1);
    }

    @Test
    void test_updateTask_statusPriority() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task2));
        taskService.updateTask(2L, null, null, null, Task.PriorityLevel.HIGH, Task.Status.COMPLETED);
        assertEquals(task2.getStatus(), Task.Status.COMPLETED);
        assertEquals(task2.getPriority(), Task.PriorityLevel.HIGH);
        long diff = Duration.between(LocalDateTime.now(), task2.getResolvedAt()).toMinutes();
        assertTrue(diff <= 1);
    }
}