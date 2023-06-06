package com.frsi.itoss.mgr.integration;

import java.io.Serializable;

public class TicketPayloadDecorator implements Serializable {


    public DashboardEntryPayloadDecorator dashboardEntry;
    public CtPayloadDecorator ct;

    public TicketPayloadDecorator(CtPayloadDecorator ct, DashboardEntryPayloadDecorator de) {
        this.ct = ct;
        this.dashboardEntry = de;
    }


}
