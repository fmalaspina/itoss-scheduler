package com.frsi.itoss.shared;

import java.io.Serializable;

public class ApiResponse implements Serializable {
    public boolean success;

    public ApiResponse(boolean success) {
        this.success = success;
    }

    public ApiResponse() {

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
