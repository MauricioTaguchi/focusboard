package com.example.focusboard;

import java.util.Objects;

public final class Task {
    private final String title;
    private final Priority priority;
    private boolean completed;

    public Task(String title, Priority priority) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title cannot be empty.");
        }
        this.title = title.trim();
        this.priority = Objects.requireNonNull(priority, "priority");
    }

    public String getTitle() {
        return title;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void toggleCompleted() {
        completed = !completed;
    }
}
