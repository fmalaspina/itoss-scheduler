package com.frsi.itoss.mgr.controllers;

import java.util.Map;

// Define a class to represent the structure of each category and its severities
public class CategoryScore {
    private String name;
    private Map<String, Integer> scores;

    public CategoryScore(String name, Map<String, Integer> scores) {
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
