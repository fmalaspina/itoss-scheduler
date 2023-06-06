package com.frsi.itoss.mgr.controllers;

import java.util.List;
import java.util.Map;

public class TypeAttendedScore {
    private final String name;
    private final String path;
    private final Map<String, Integer> scores;
    private final List<EnvironmentScore> environmentScores;


    public TypeAttendedScore(String name, String path, Map<String, Integer> scores, List<EnvironmentScore> environmentScores) {
        this.name = name;
        this.path = path;
        this.scores = scores;
        this.environmentScores = environmentScores;

    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public List<EnvironmentScore> getEnvironmentScores() {
        return environmentScores;
    }


}
