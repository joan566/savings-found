package com.playground.fondoahorro.presentation.shared;

import java.util.Locale;

public final class PersonInitials {

    private PersonInitials() {
    }

    public static String fromName(String name) {
        if (name == null || name.isBlank()) {
            return "?";
        }

        String[] parts = name.strip().split("\\s+");
        String initials = parts[0].substring(0, 1);
        if (parts.length > 1) {
            initials += parts[1].substring(0, 1);
        } else if (parts[0].length() > 1) {
            initials += parts[0].substring(1, 2);
        }
        return initials.toUpperCase(Locale.ROOT);
    }
}
