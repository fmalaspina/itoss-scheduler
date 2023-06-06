package com.frsi.itoss.shared;

import java.io.Serializable;

public enum ItossOperation implements Serializable {
    SAVE_DASHBOARD_ENTRY,
    SAVE_CT_STATUS,
    SAVE_MONITOR_CT_STATUS,
    REMOVE_DASHBOARD_ENTRY,
    SEND_MAIL,
    SEND_NOTIFICATION,
    SAVE_METRIC,
    EXECUTE_TASK
}
