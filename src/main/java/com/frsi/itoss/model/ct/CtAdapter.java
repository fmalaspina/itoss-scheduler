package com.frsi.itoss.model.ct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CtAdapter implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;

}
