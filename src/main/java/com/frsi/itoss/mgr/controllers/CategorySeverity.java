package com.frsi.itoss.mgr.controllers;

import java.util.Map;

// Define a class to represent the structure of each category and its severities
public class CategorySeverity {
    private String name;
    private Map<String, Integer> severities;

    public CategorySeverity(String name, Map<String, Integer> severities) {
        this.name = name;
        this.severities = severities;
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getSeverities() {
        return severities;
    }
}
