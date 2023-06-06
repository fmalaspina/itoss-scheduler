package com.frsi.itoss.mgr.integration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frsi.itoss.shared.DashboardEntry;
import com.frsi.itoss.shared.DashboardEntryKey;
import com.frsi.itoss.shared.MetricPayloadData;

import java.io.Serializable;
import java.util.Date;

public class DashboardEntryPayloadDecorator implements Serializable {

    @JsonIgnore
    public DashboardEntry dashboardEntry;

    public DashboardEntryPayloadDecorator(DashboardEntry de) {

        this.dashboardEntry = de;
    }

    public DashboardEntryKey getId() {
        return dashboardEntry.getId();
    }

    public boolean isFault() {
        return dashboardEntry.isFault();
    }

    public Date getCreatedAt() {
        return dashboardEntry.getCreatedAt();
    }

    public Long getContainerId() {
        return dashboardEntry.getContainerId();
    }

    public Date getModifiedAt() {
        return dashboardEntry.getModifiedAt();
    }

    public String getSeverity() {
        return dashboardEntry.getSeverity();
    }

    public Date getLastChange() {
        return dashboardEntry.getLastChange();
    }

    public Boolean getAttended() {
        return dashboardEntry.getAttended();
    }

    public float getScore() {
        return dashboardEntry.getScore();
    }

    public Long getCompanyId() {
        return dashboardEntry.getCompanyId();
    }

    public Long getRuleId() {
        return dashboardEntry.getRuleId();
    }

    public MetricPayloadData getMetricPayloadData() {
        return dashboardEntry.getMetricPayloadData();
    }

    public String getRuleDescription() {
        return dashboardEntry.getRuleDescription();
    }

}
