package com.frsi.itoss.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CollectorResponse implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<PayloadDataResult> payloadDataResult = new ArrayList<>();
    private String error;
    private String metricName;
    private String monitorName;
    private MetricCategory metricCategory;
    private Long metricId;
    private Long ctId;
    private Long monitorId;
    private boolean statusCollection;
    private String formated;
    private CtConfiguration ct;
    private String rawData;

    @JsonIgnore
    public List<String> getRawResults() {
        return payloadDataResult.stream().map(r -> r.getRawResult()).collect(Collectors.toList());
    }


}
