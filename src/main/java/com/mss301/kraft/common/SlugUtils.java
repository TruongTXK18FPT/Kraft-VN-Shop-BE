package com.mss301.kraft.common;

public final class SlugUtils {

    private SlugUtils() {
    }

    public static String toSlug(String input) {
        if (input == null) {
            return null;
        }
        String slug = input
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        return slug.isEmpty() ? null : slug;
    }
}
