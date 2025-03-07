package com.devsoft.rgdi_store.validation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CustomError {
    private Instant timestamp;
    private Integer status;
    private String error;
    private String path;
    private List<FieldMessage> fieldErrors = new ArrayList<>(); //também pega erro de campos

    public CustomError(Instant timestamp, Integer status, String error, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }

    public List<FieldMessage> getFieldErrors() {
        return fieldErrors;
    }

    public void addFieldError(String fieldname, String message) {
        this.fieldErrors.add(new FieldMessage(fieldname, message));
    }
}
