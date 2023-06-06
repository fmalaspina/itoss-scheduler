package com.frsi.itoss.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface MetricColumn {
    @JsonIgnore
    boolean isBootTime();

    int getOrdinal();

    void setOrdinal(int ordinal);

    String getName();

    void setName(String name);

    ProcessingPhase getPhase();

    void setPhase(ProcessingPhase phase);

    String getColumnName();

    void setColumnName(String columnName);

    String getPath();

    void setPath(String path);

    DataType getType();

    void setType(DataType type);

    String getUnit();

    void setUnit(String unit);

    String getFormat();

    void setFormat(String format);

    InternalField getInternalField();

    void setInternalField(InternalField internalField);
}
