package com.frsi.itoss.mgr.controllers;

import java.util.Map;

public class EnvironmentScore {
    private String name;
    private Map<String, Integer> scores;

    public EnvironmentScore(String name, Map<String, Integer> scores) {
        this.name = name;
        this.scores = scores;
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }
}
