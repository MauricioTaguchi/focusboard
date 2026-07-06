package com.example.focusboard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

final class TaskRepository {
    private static final String APP_DIRECTORY = ".focusboard";
    private static final String FILE_NAME = "tasks.tsv";

    private final Path storagePath;

    TaskRepository() {
        storagePath = Path.of(System.getProperty("user.home"), APP_DIRECTORY, FILE_NAME);
    }

    List<Task> load() throws IOException {
        if (!Files.exists(storagePath)) {
            return List.of();
        }

        List<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(storagePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line).ifPresent(tasks::add);
            }
        }
        return tasks;
    }

    void save(List<Task> tasks) throws IOException {
        Files.createDirectories(storagePath.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(storagePath, StandardCharsets.UTF_8)) {
            for (Task task : tasks) {
                writer.write(task.getPriority().name());
                writer.write('\t');
                writer.write(Boolean.toString(task.isCompleted()));
                writer.write('\t');
                writer.write(encode(task.getTitle()));
                writer.newLine();
            }
        }
    }

    private Optional<Task> parseLine(String line) {
        if (line == null || line.isBlank()) {
            return Optional.empty();
        }

        String[] parts = line.split("\t", 3);
        if (parts.length != 3) {
            return Optional.empty();
        }

        try {
            Priority priority = Priority.valueOf(parts[0]);
            boolean completed = Boolean.parseBoolean(parts[1]);
            Task task = new Task(decode(parts[2]), priority);
            task.setCompleted(completed);
            return Optional.of(task);
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        byte[] decoded = Base64.getUrlDecoder().decode(value);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}
