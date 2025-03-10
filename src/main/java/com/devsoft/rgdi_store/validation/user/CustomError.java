package com.devsoft.rgdi_store.validation.user;

public class CustomError {    
    private Integer status;
    private String error;    

    public CustomError(Integer status, String error) {
        this.status = status;
        this.error = error;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;    }

    /*
    public List<FieldMessage> getFieldErrors() {
        return fieldErrors;
    }

    public void addFieldError(String fieldname, String message) {
        this.fieldErrors.add(new FieldMessage(fieldname, message));
    }
    */
}
