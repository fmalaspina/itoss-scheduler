package com.frsi.itoss.mgr.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.frsi.itoss.shared.*;

import java.util.Set;

//@JsonRpcService("/managerApi")

public interface ManagerAPI {
    @Deprecated
    boolean saveCtStatus(/*@JsonRpcParam(value = "ctStatus") */CtStatus ctStatus);

    CollectorConfiguration getConfiguration(/*@JsonRpcParam(value = "collectorId")*/ Long collectorId);

    Set<CtConfiguration> getCts(/*@JsonRpcParam(value = "collectorId")*/ Long collectorId);

    Set<MonitorConfiguration> getMonitors(/*@JsonRpcParam(value = "collectorId")*/ Long collectorId) throws JsonProcessingException;

    @Deprecated
    boolean saveDashboardEntry(
            /*@JsonRpcParam(value = "dashboardEntryPayload")*/ DashboardEntryPayload dashboardEntryPayload);

    @Deprecated
    boolean sendNotification(/*@JsonRpcParam(value = "notification")*/ Notification notification);


    @Deprecated
    boolean deleteDashboardEntry(/*@JsonRpcParam(value = "dashboardEntryId")*/ DashboardEntryKey dashboardEntryKey);

    @Deprecated
    boolean sendMail(/*@JsonRpcParam(value = "destinations")*/ String[] destinations,
            /*@JsonRpcParam(value = "message") */String message,
            /*@JsonRpcParam(value = "metricPayloadData") */MetricPayloadData metricPayloadData,
            /*@JsonRpcParam(value = "toolId")*/ Long toolId);

    boolean ping();

    @Deprecated
    boolean persistDashboardEntry(
            /*@JsonRpcParam(value = "dashboardEntryPayload") */DashboardEntryPayload dashboardEntryPayload);

    @Deprecated
    boolean cleanFaultedDashboardEntries(/*@JsonRpcParam(value = "monitorCtKeys")*/ Set<MonitorCtKey> monitorCtKeys);

    @Deprecated
    boolean cleanNonFaultedDashboardEntries(/*@JsonRpcParam(value = "monitorCtKeys")*/ Set<MonitorCtKey> monitorCtKeys);

    @Deprecated
    boolean removeDashboardEntry(/*@JsonRpcParam(value = "dashboardEntryId") */DashboardEntryKey dashboardEntryKey);

    @Deprecated
    boolean setMonitorCtStatus(/*@JsonRpcParam(value = "monitorCtStatus")*/ Set<MonitorCtStatus> monitorCtStatusSet);

    ApiResponse sendMessage(/*@JsonRpcParam(value = "message")*/ItossMessage message);
}