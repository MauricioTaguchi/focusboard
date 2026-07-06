package com.example.focusboard;

import java.util.Locale;

public enum Priority {
    LOW,
    MEDIUM,
    HIGH;

    @Override
    public String toString() {
        String text = name().toLowerCase(Locale.ENGLISH);
        return text.substring(0, 1).toUpperCase(Locale.ENGLISH) + text.substring(1);
    }
}
