package com.devsoft.rgdi_store.validation.base;

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
}
