package com.frsi.itoss.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Field implements Serializable, MetricColumn {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int ordinal;
    private String name;
    private ProcessingPhase phase = ProcessingPhase.Detail;
    /**
     * Name of source field of database to be taken with jdbc instrumentation
     */
    private String columnName = "";
    /**
     * json path for rest api instrumentation
     */
    private String path = "";
    private DataType type;
    private String unit = "NA";
    private String format;
    private InternalField internalField;

    @Override
    @JsonIgnore
    public boolean isBootTime() {
        return internalField == InternalField.BOOTTIME;
    }

}
