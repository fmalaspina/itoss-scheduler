package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardEntryPayload implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private DashboardEntryKey key;
    private Long containerId;
    private String severity;
    private MetricPayloadData metricPayloadData;
    private Long ruleId;
    private String ruleDescription;
    private Date lastChange;
    private float score;
    private Long companyId;

    public boolean isFault() {
        return (boolean) this.metricPayloadData.getFields().get("fault");
    }


}