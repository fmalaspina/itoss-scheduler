package com.frsi.itoss.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MetricPayloadData implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Map<String, Object> tags = new HashMap<String, Object>();
    private Map<String, Object> fields = new HashMap<String, Object>();
    private String metricName;
    private ProcessingPhase phase;
    private Long metricId;
    private Long ctId;
    private Long monitorId;
    private String rawResult;
    private String time = DateTimeFormatter.ISO_DATE_TIME.format(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")));
    private ArrayList<RuleEvaluationResult> evaluationResult = new ArrayList<>();
    private List<String> actionLog = new ArrayList<>();

    @JsonIgnore
    public Map<String, String> getFieldsAsStrings() {
        Map<String, String> newFields = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : this.fields.entrySet()) {
            newFields.put(entry.getKey(), (entry.getValue() == null) ? Constants.BLANK : entry.getValue().toString());

        }
        return newFields;
    }

    @JsonIgnore
    public Map<String, String> getTagsAsStrings() {
        Map<String, String> newTags = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : this.tags.entrySet()) {
            newTags.put(entry.getKey(), (entry.getValue() == null) ? Constants.BLANK : entry.getValue().toString());

        }
        return newTags;
    }

    @JsonIgnore
    public boolean isFault() {
        return fields.entrySet().stream().anyMatch(e -> e.getKey().equals("fault") && e.getValue().equals(true));
    }

    @JsonIgnore
    public String getBoottime() {
        if (fields.get("itoss_boottime") != null) {
            return fields.get("itoss_boottime").toString();
        } else {
            return null;
        }
    }

}
