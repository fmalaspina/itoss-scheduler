package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeAttribute implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name;
    // text , number , boolean, list, float
    private DataType type;
    private String size;
    private String group;
    private boolean isIdPart;
    private boolean required;
    private List<Object> listValues;
    private int order;
    private String formatType;


}