package com.frsi.itoss.mgr.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ContinuousQueryAdapter implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public String name;
    public String query;
}
