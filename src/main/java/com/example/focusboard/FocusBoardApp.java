package com.example.focusboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class FocusBoardApp extends JFrame {
    private static final Color BACKGROUND = new Color(246, 247, 249);
    private static final Color SURFACE = Color.WHITE;
    private static final Color BORDER = new Color(229, 231, 235);
    private static final Color TEXT = new Color(31, 41, 55);
    private static final Color MUTED_TEXT = new Color(75, 85, 99);
    private static final Color PRIMARY = new Color(37, 99, 235);

    private final TaskRepository repository = new TaskRepository();
    private final TaskTableModel tableModel = new TaskTableModel();
    private final JTable taskTable = new JTable(tableModel);
    private final JTextField titleField = new JTextField();
    private final JTextField searchField = new JTextField();
    private final JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
    private final JComboBox<FilterMode> filterBox = new JComboBox<>(FilterMode.values());
    private final JLabel totalLabel = new JLabel("0 total");
    private final JLabel activeLabel = new JLabel("0 active");
    private final JLabel completedLabel = new JLabel("0 completed");
    private final JLabel statusLabel = new JLabel("Ready");

    public FocusBoardApp() {
        super("FocusBoard");
        configureLookAndFeel();
        configureFrame();
        setContentPane(createContent());
        configureInteractions();
        loadInitialTasks();
        refreshStats();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FocusBoardApp().setVisible(true));
    }

    private void configureFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(920, 600));
        setLocationRelativeTo(null);
    }

    private JPanel createContent() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(BACKGROUND);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createMainPanel(), BorderLayout.CENTER);
        root.add(createStatsPanel(), BorderLayout.SOUTH);

        return root;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);

        JLabel title = new JLabel("FocusBoard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("A focused desktop task board built with Java Swing.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED_TEXT);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout(16, 0));
        main.setOpaque(false);

        main.add(createFormPanel(), BorderLayout.WEST);
        main.add(createTablePanel(), BorderLayout.CENTER);
        return main;
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setPreferredSize(new Dimension(260, 100));
        form.setBackground(SURFACE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.weightx = 1;

        JLabel formTitle = new JLabel("New Task");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(TEXT);
        addFormRow(form, formTitle, gbc);

        addFormRow(form, createFieldLabel("Task title"), gbc);
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addFormRow(form, titleField, gbc);

        addFormRow(form, createFieldLabel("Priority"), gbc);
        priorityBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addFormRow(form, priorityBox, gbc);

        JButton addButton = createPrimaryButton("Add Task");
        addButton.addActionListener(this::addTask);
        addFormRow(form, addButton, gbc);

        JButton completeButton = createSecondaryButton("Toggle Complete");
        completeButton.addActionListener(this::toggleSelectedTask);
        addFormRow(form, completeButton, gbc);

        JButton clearCompletedButton = createSecondaryButton("Clear Completed");
        clearCompletedButton.addActionListener(this::clearCompletedTasks);
        addFormRow(form, clearCompletedButton, gbc);

        JButton deleteButton = createDangerButton("Delete Selected");
        deleteButton.addActionListener(this::deleteSelectedTask);
        addFormRow(form, deleteButton, gbc);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        gbc.weighty = 1;
        form.add(spacer, gbc);

        return form;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(MUTED_TEXT);
        return label;
    }

    private void addFormRow(JPanel panel, Component component, GridBagConstraints gbc) {
        gbc.gridy++;
        panel.add(component, gbc);
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 12));
        tablePanel.setOpaque(false);

        tablePanel.add(createTableControls(), BorderLayout.NORTH);
        tablePanel.add(createTaskTable(), BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createTableControls() {
        JPanel controls = new JPanel(new BorderLayout());
        controls.setOpaque(false);

        JLabel taskListLabel = new JLabel("Task List");
        taskListLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        taskListLabel.setForeground(TEXT);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);

        searchField.setPreferredSize(new Dimension(190, 30));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.putClientProperty("JTextField.placeholderText", "Search tasks");

        filterBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        filterPanel.add(searchField);
        filterPanel.add(filterBox);

        controls.add(taskListLabel, BorderLayout.WEST);
        controls.add(filterPanel, BorderLayout.EAST);
        return controls;
    }

    private JScrollPane createTaskTable() {
        taskTable.setRowHeight(36);
        taskTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        taskTable.getTableHeader().setBackground(new Color(243, 244, 246));
        taskTable.getTableHeader().setForeground(new Color(55, 65, 81));
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setFillsViewportHeight(true);
        taskTable.setDefaultRenderer(Object.class, new TaskCellRenderer());
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(390);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(110);

        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        return scrollPane;
    }

    private JPanel createStatsPanel() {
        JPanel stats = new JPanel(new GridBagLayout());
        stats.setBackground(SURFACE);
        stats.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 16, 14, 16)
        ));

        addStat(stats, totalLabel, 0);
        addStat(stats, activeLabel, 1);
        addStat(stats, completedLabel, 2);

        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        statusLabel.setForeground(MUTED_TEXT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        stats.add(statusLabel, gbc);

        return stats;
    }

    private void addStat(JPanel stats, JLabel label, int column) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setForeground(TEXT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = column;
        gbc.gridy = 0;
        gbc.weightx = column == 0 ? 0.7 : 0.8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        stats.add(label, gbc);
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(229, 231, 235));
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = createSecondaryButton(text);
        button.setForeground(new Color(185, 28, 28));
        return button;
    }

    private void configureInteractions() {
        titleField.addActionListener(this::addTask);

        filterBox.addActionListener(event -> {
            tableModel.setFilter((FilterMode) filterBox.getSelectedItem());
            refreshStats();
        });

        searchField.addActionListener(event -> applySearch());
        searchField.getDocument().addDocumentListener(new SimpleDocumentListener(this::applySearch));

        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int selectedColumn = taskTable.getSelectedColumn();
                if (event.getClickCount() == 2 && taskTable.getSelectedRow() >= 0 && selectedColumn != 0) {
                    toggleSelectedTask(null);
                }
            }
        });

        tableModel.setOnTasksChanged(() -> {
            persistTasks("Saved locally");
            refreshStats();
        });
    }

    private void applySearch() {
        tableModel.setSearchQuery(searchField.getText());
        refreshStats();
    }

    private void addTask(ActionEvent event) {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a task title.", "Missing Title", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Priority priority = (Priority) priorityBox.getSelectedItem();
        tableModel.addTask(new Task(title, priority == null ? Priority.MEDIUM : priority));
        titleField.setText("");
        titleField.requestFocusInWindow();
    }

    private void toggleSelectedTask(ActionEvent event) {
        int row = taskTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a task first.", "No Task Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        tableModel.toggleComplete(row);
    }

    private void clearCompletedTasks(ActionEvent event) {
        int removed = tableModel.clearCompleted();
        if (removed == 0) {
            statusLabel.setText("No completed tasks to clear");
        }
    }

    private void deleteSelectedTask(ActionEvent event) {
        int row = taskTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a task first.", "No Task Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        tableModel.deleteTask(row);
    }

    private void refreshStats() {
        int total = tableModel.getTotalCount();
        int completed = tableModel.getCompletedCount();
        int active = total - completed;

        totalLabel.setText(total + " total");
        activeLabel.setText(active + " active");
        completedLabel.setText(completed + " completed");
    }

    private void loadInitialTasks() {
        try {
            List<Task> savedTasks = repository.load();
            if (savedTasks.isEmpty()) {
                tableModel.replaceTasks(sampleTasks());
                persistTasks("Sample tasks loaded");
            } else {
                tableModel.replaceTasks(savedTasks);
                statusLabel.setText("Loaded saved tasks");
            }
        } catch (IOException exception) {
            tableModel.replaceTasks(sampleTasks());
            statusLabel.setText("Started with sample tasks");
        }
    }

    private List<Task> sampleTasks() {
        return List.of(
                new Task("Design the project README", Priority.HIGH),
                new Task("Create the Swing layout", Priority.MEDIUM),
                new Task("Prepare the GitHub repository", Priority.LOW)
        );
    }

    private void persistTasks(String successMessage) {
        try {
            repository.save(tableModel.getAllTasks());
            statusLabel.setText(successMessage);
        } catch (IOException exception) {
            statusLabel.setText("Changes are not saved");
        }
    }

    private void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing falls back to the default look and feel.
        }
    }

    private static final class TaskCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                component.setForeground(TEXT);
            }
            setBorder(new EmptyBorder(0, 10, 0, 10));
            return component;
        }
    }
}
