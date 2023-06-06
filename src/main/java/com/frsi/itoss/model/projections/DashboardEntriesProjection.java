package com.frsi.itoss.model.projections;

public interface DashboardEntriesProjection {
    String getEnvironment();
    String getCtTypePath();
    String getMetricCategory();
    String getCtType();
    Long getCtTypeId();
    String getSeverity();
    String getScore();
    int getTotal();
}
