package com.noubase.idema.controller;

import com.noubase.idema.exception.DuplicateFieldException;
import com.noubase.idema.exception.ResourceNotFoundException;
import com.noubase.idema.exception.ResourcesNotFoundException;
import com.noubase.idema.model.error.ErrorResource;
import com.noubase.idema.model.error.FieldErrorResource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rshuper on 9/23/14.
 * © egocube.com
 */
@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);


    @NotNull
    private ErrorResource handle(@NotNull BindingResult result) {
        ErrorResource error = new ErrorResource("InvalidRequest", "Invalid " + result.getTarget().getClass().getSimpleName());
        error.setFieldErrors(new ArrayList<>());
        List<FieldError> fieldErrors = result.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            FieldErrorResource e = new FieldErrorResource();
            e.setResource(fieldError.getObjectName());
            e.setField(fieldError.getField());
            e.setCode(fieldError.getCode());
            e.setMessage(fieldError.getDefaultMessage());
            error.getFieldErrors().add(e);
        }
        return error;
    }


    @NotNull
    @ResponseBody
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFoundRequest(@NotNull ResourceNotFoundException e) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", e.getMessage());
        map.put("resource", e.getResource());
        map.put("id", e instanceof ResourcesNotFoundException ? ((ResourcesNotFoundException) e).getIds() : e.getId());
        logger.warn("404. " + map);
        return map;
    }

    @NotNull
    @ExceptionHandler(DuplicateFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleNotFoundRequest(@NotNull DuplicateFieldException e) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", e.getMessage());
        map.put("resource", e.getResource());
        map.put("field", e.getField());
        return map;
    }

    @NotNull
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ResponseBody
    public Map<String, Object> handleUnsupportedMediaTypeException(@NotNull HttpMediaTypeNotSupportedException ex) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("message", "Unsupported Media Type");
        map.put("supported", ex.getSupportedMediaTypes());
        return map;
    }

    @NotNull
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleUncaughtException(@NotNull Exception ex) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("error", "Internal server error. Please try again later.");
        logger.error("500. " + ex.getMessage(), ex);
        return map;
    }

    @NotNull
    @ResponseBody
    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResource handleInvalidRequest(@NotNull BindException e) {
        return handle(e.getBindingResult());
    }

    @NotNull
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResource handleInvalidRequest(@NotNull MethodArgumentNotValidException e) {
        return handle(e.getBindingResult());
    }
}

