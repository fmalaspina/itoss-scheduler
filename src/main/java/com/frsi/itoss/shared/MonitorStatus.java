package com.frsi.itoss.shared;

import java.io.Serializable;

public enum MonitorStatus implements Serializable {
    FAULT,
    SECONDARY_FAULT,
    OK
}
