package com.frsi.itoss.mgr.outage;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutageScheduleResponse implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private boolean success;
    private String jobId;
    private String jobGroup;
    private String message;

    public OutageScheduleResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public OutageScheduleResponse(boolean success, String jobId, String jobGroup, String message) {
        this.success = success;
        this.jobId = jobId;
        this.jobGroup = jobGroup;
        this.message = message;
    }

    // Getters and Setters (Omitted for brevity)
}