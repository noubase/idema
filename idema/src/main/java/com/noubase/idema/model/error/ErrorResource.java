package com.noubase.idema.model.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResource {
    private final String code;
    private final String message;
    private List<FieldErrorResource> fieldErrors;

    public ErrorResource(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<FieldErrorResource> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldErrorResource> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    @NotNull
    @Override
    public String toString() {
        return "ErrorResource {" +
                " code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}