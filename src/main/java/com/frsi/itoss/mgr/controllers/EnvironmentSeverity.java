package com.frsi.itoss.mgr.controllers;

import java.util.Map;

public class EnvironmentSeverity {
    private String name;
    private Map<String, Integer> severities;

    public EnvironmentSeverity(String name, Map<String, Integer> severities) {
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
