package com.copo.app.service;

import java.util.Map;

public class ExamTypeNormalizer {

    private static final Map<String, String> EXAM_TYPE_MAP = Map.ofEntries(
        Map.entry("cat1", "CAT 1"),
        Map.entry("cat 1", "CAT 1"),
        Map.entry("cat-1", "CAT 1"),
        Map.entry("cat2", "CAT 2"),
        Map.entry("cat 2", "CAT 2"),
        Map.entry("cat-2", "CAT 2"),
        Map.entry("model", "Model"),
        Map.entry("cat1 lab", "CAT 1 Lab"),
        Map.entry("cat 1 lab", "CAT 1 Lab"),
        Map.entry("cat-1 lab", "CAT 1 Lab"),
        Map.entry("cat2 lab", "CAT 2 Lab"),
        Map.entry("cat 2 lab", "CAT 2 Lab"),
        Map.entry("cat-2 lab", "CAT 2 Lab"),
        Map.entry("model lab", "Model Lab")
    );

    public static String normalize(String rawExamType) {
        if (rawExamType == null || rawExamType.isBlank()) return null;

        // Normalize spaces and convert to lowercase
        String cleaned = rawExamType.trim().toLowerCase().replaceAll("\\s+", " ");

        return EXAM_TYPE_MAP.getOrDefault(cleaned, rawExamType.trim());
    }
}
