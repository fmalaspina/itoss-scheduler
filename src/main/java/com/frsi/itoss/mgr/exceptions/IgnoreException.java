package com.frsi.itoss.mgr.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class IgnoreException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IgnoreException(String message) {
        super(message);
    }
}

