package com.craftworks.TaskManagementSystem.Task;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Entity
@Table
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Task {
    public enum PriorityLevel {LOW, MEDIUM, HIGH}

    public enum Status {NOT_STARTED, IN_PROGRESS, BLOCKED, COMPLETED}

    @Id
    @SequenceGenerator(
            name = "task_seq",
            sequenceName = "task_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "task_seq"
    )
    private Long id;
    @CreationTimestamp
    private LocalDate createdAt;
    @UpdateTimestamp
    private LocalDate updatedAt;
    @Column
    @NotNull
    private LocalDate dueDate;
    @Column
    private LocalDate resolvedAt;
    @Column
    @NotNull
    @NotBlank
    private String title;
    @Column
    @NotNull
    private String description;
    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private PriorityLevel priority;
    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.NOT_STARTED;

    public Task(String title, String description, PriorityLevel priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        if (dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid due date. Due date before creation time.");
        }
        this.dueDate = dueDate;
    }

    public void setTitle(String title) {
        if (title.isEmpty()) {
            throw new IllegalArgumentException("Invalid title. Title cannot be empty.");
        }
        this.title = title;
    }

    public void setStatus(Status status) {
        if (status == Task.Status.COMPLETED) {
            this.setResolvedAt(LocalDate.now());
        }
        this.status = status;
    }
}
