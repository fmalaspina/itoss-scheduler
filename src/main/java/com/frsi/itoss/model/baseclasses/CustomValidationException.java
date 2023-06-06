package com.frsi.itoss.model.baseclasses;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class CustomValidationException extends RuntimeException {

    /**
     *
     */

    private static final long serialVersionUID = 1L;
    private String validationErrors;

    public CustomValidationException() {
        super();
        this.validationErrors = "";
    }

    public CustomValidationException(String validationErrors) {
        super();
        //if (this.validationErrors == null) {
        this.validationErrors = validationErrors;
        //} else {
    }
    public void setValidationErrors(String validationErrors) {
            this.validationErrors = this.validationErrors + "," + validationErrors;
    }

}
