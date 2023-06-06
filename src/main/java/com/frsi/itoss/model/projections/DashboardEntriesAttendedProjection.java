package com.frsi.itoss.model.projections;

public interface DashboardEntriesAttendedProjection {
    String getEnvironment();
    String getCtTypePath();
    String getCtType();
    String getSeverity();

    int getTotal();
}
