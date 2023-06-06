package com.frsi.itoss.shared;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class DashboardEntryMessageReport implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private List<DashboardEntry> dashboardEntryPayload = new ArrayList<>();
}
