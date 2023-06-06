package com.frsi.itoss.model.dashboard;

import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.profile.Monitor;
import com.frsi.itoss.shared.DashboardEntry;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor

@Data
public class DashboardEntryAndRelated implements Serializable {

    /**
     * Dashboard entry wrapper for graphql endpoint
     */

    private static final long serialVersionUID = 1L;

    private Ct ct;
    private Monitor monitor;
    private DashboardEntry dashboardEntry;

    public DashboardEntryAndRelated(Ct c, Monitor m, DashboardEntry d) {
        this.ct = c;
        this.monitor = m;
        this.dashboardEntry = d;
    }

}
