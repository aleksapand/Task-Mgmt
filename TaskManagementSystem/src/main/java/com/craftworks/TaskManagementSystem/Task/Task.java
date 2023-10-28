package com.craftworks.TaskManagementSystem.Task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties("id")
public class Task {
    public enum PriorityLevel {LOW, MEDIUM, HIGH}

    public enum Status {NOT_STARTED, IN_PROGRESS, BLOCKED, COMPLETED}

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column
    @NotNull
    private LocalDateTime dueDate;
    @Column
    private LocalDateTime resolvedAt;
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

    public Task(String title, String description, PriorityLevel priority, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        if (dueDate.isBefore(LocalDateTime.now())) {
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
            this.setResolvedAt(LocalDateTime.now());
        }
        this.status = status;
    }
}
