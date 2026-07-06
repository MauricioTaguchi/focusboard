package com.example.focusboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

public final class TaskTableModel extends AbstractTableModel {
    private final String[] columns = {"Done", "Task", "Priority", "Status"};
    private final List<Task> tasks = new ArrayList<>();
    private FilterMode filter = FilterMode.ALL;
    private String searchQuery = "";
    private Runnable onTasksChanged = () -> {
    };

    @Override
    public int getRowCount() {
        return getVisibleTasks().size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? Boolean.class : String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task task = getVisibleTasks().get(rowIndex);
        return switch (columnIndex) {
            case 0 -> task.isCompleted();
            case 1 -> task.getTitle();
            case 2 -> task.getPriority().toString();
            case 3 -> task.isCompleted() ? "Completed" : "Active";
            default -> "";
        };
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex != 0) {
            return;
        }

        Task task = getVisibleTasks().get(rowIndex);
        task.setCompleted(Boolean.TRUE.equals(value));
        fireTableRowsUpdated(rowIndex, rowIndex);
        notifyTasksChanged();
    }

    public void addTask(Task task) {
        tasks.add(task);
        fireTableDataChanged();
        notifyTasksChanged();
    }

    public void toggleComplete(int visibleRow) {
        Task task = getVisibleTasks().get(visibleRow);
        task.toggleCompleted();
        fireTableDataChanged();
        notifyTasksChanged();
    }

    public void deleteTask(int visibleRow) {
        Task task = getVisibleTasks().get(visibleRow);
        tasks.remove(task);
        fireTableDataChanged();
        notifyTasksChanged();
    }

    public int clearCompleted() {
        int originalSize = tasks.size();
        tasks.removeIf(Task::isCompleted);
        int removed = originalSize - tasks.size();
        if (removed > 0) {
            fireTableDataChanged();
            notifyTasksChanged();
        }
        return removed;
    }

    public void replaceTasks(List<Task> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
        fireTableDataChanged();
    }

    public void setFilter(FilterMode filter) {
        this.filter = filter == null ? FilterMode.ALL : filter;
        fireTableDataChanged();
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery == null ? "" : searchQuery.trim().toLowerCase(Locale.ENGLISH);
        fireTableDataChanged();
    }

    public void setOnTasksChanged(Runnable onTasksChanged) {
        this.onTasksChanged = onTasksChanged == null ? () -> {
        } : onTasksChanged;
    }

    public int getTotalCount() {
        return tasks.size();
    }

    public int getCompletedCount() {
        int completed = 0;
        for (Task task : tasks) {
            if (task.isCompleted()) {
                completed++;
            }
        }
        return completed;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    private List<Task> getVisibleTasks() {
        return tasks.stream()
                .filter(this::matchesFilter)
                .filter(this::matchesSearch)
                .toList();
    }

    private boolean matchesFilter(Task task) {
        return filter == FilterMode.ALL
                || filter == FilterMode.ACTIVE && !task.isCompleted()
                || filter == FilterMode.COMPLETED && task.isCompleted();
    }

    private boolean matchesSearch(Task task) {
        if (searchQuery.isBlank()) {
            return true;
        }
        return task.getTitle().toLowerCase(Locale.ENGLISH).contains(searchQuery)
                || task.getPriority().toString().toLowerCase(Locale.ENGLISH).contains(searchQuery);
    }

    private void notifyTasksChanged() {
        onTasksChanged.run();
    }
}
