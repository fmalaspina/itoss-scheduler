package com.frsi.itoss.mgr.controllers;

import java.util.List;
import java.util.Map;

public class TypeAttendedSeverity {
    private final String name;
    private final String path;
    private final Map<String, Integer> severities;
    private final List<EnvironmentSeverity> environmentSeverities;


    public TypeAttendedSeverity(String name, String path, Map<String, Integer> severities, List<EnvironmentSeverity> environmentSeverities) {
        this.name = name;
        this.path = path;
        this.severities = severities;
        this.environmentSeverities = environmentSeverities;

    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getSeverities() {
        return severities;
    }

    public List<EnvironmentSeverity> getEnvironmentSeverities() {
        return environmentSeverities;
    }


}
