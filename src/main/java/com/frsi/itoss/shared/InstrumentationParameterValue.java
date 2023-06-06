package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class InstrumentationParameterValue<T> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private T value;
}
