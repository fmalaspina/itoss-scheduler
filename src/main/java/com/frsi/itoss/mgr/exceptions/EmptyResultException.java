package com.frsi.itoss.mgr.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmptyResultException extends RuntimeException {

    public EmptyResultException(String msg) {
        super(msg);
    }

    public EmptyResultException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
