package com.craftworks.TaskManagementSystem.Task;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table
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
    @Column
    private LocalDate createdAt;
    @Column
    private LocalDate updatedAt;
    @Column
    private LocalDate dueDate;
    @Column
    private LocalDate resolvedAt;
    @Column
    private String title;
    @Column
    private String description;
    @Column
    @Enumerated(EnumType.STRING)
    private PriorityLevel priority;
    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    public Task(String title, String description, PriorityLevel priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;

        this.createdAt = LocalDate.now();
        this.status = Status.NOT_STARTED;
    }
    public Task() {

    }

    public Long getId() {
        return id;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        if(dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid due date. Due date before creation time.");
        }
        this.dueDate = dueDate;
    }

    public LocalDate getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDate resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        if(title.isEmpty()) {
            throw new IllegalArgumentException("Invalid title. Title cannot be empty.");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public void setPriority(PriorityLevel priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status == Task.Status.COMPLETED) {
            this.setResolvedAt(LocalDate.now());
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", dueDate=" + dueDate +
                ", resolvedAt=" + resolvedAt +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
