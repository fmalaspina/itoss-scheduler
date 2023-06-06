package com.frsi.itoss.mgr.controllers;

import java.util.List;
import java.util.Map;

public class TypeScore {
    private final String name;

    public Long getId() {
        return id;
    }

    private final Long id;
    private final String path;
    private final Map<String, Integer> scores;
    private final List<EnvironmentScore> environmentScore;
    private final List<CategoryScore> categoryScore;

    public TypeScore(Long id, String name, String path, Map<String, Integer> scores, List<EnvironmentScore> environmentScore, List<CategoryScore> categoryScore) {
        this.name = name;
        this.id = id;
        this.path = path;
        this.scores = scores;
        this.environmentScore = environmentScore;
        this.categoryScore = categoryScore;
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
        return environmentScore;
    }

    public List<CategoryScore> getCategoryScores() {
        return categoryScore;
    }
}
