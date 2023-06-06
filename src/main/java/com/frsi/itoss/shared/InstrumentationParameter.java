package com.frsi.itoss.shared;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data


@NoArgsConstructor
public class InstrumentationParameter implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private Object defaultValue;
    private DataType type;
    private String formatType;
    private String subType;
    private boolean isIdPart;
    private int order;
    private List<Object> listValues;
    private Source source;

    public InstrumentationParameter(String name, Object defaultValue, DataType type, String formatType, String subType,
                                    Source source, boolean isIdPart, int order) {
        super();
        this.name = name;
        this.defaultValue = defaultValue;
        this.type = type;
        this.formatType = formatType;
        this.subType = subType;
        this.source = source;
        this.isIdPart = isIdPart;
        this.order = order;
    }

    public InstrumentationParameter(String name, Object defaultValue, DataType type, String formatType, String subType,
                                    Source source, List<Object> listValues, boolean isIdPart, int order) {
        super();
        this.name = name;
        this.defaultValue = defaultValue;
        this.type = type;
        this.formatType = formatType;
        this.subType = subType;
        this.source = source;
        this.listValues = listValues;
        this.isIdPart = isIdPart;
        this.order = order;
    }


}
