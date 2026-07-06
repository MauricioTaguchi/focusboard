package com.example.focusboard;

public enum FilterMode {
    ALL("All Tasks"),
    ACTIVE("Active"),
    COMPLETED("Completed");

    private final String label;

    FilterMode(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
