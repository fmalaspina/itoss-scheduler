package com.frsi.itoss.mgr.services;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollectorEvent<T> {
    private T payload;
    private String operation;
}
