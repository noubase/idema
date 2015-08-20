package com.noubase.core.crud.exception;

import static java.lang.String.format;

/**
 * Created by rshuper on 23.07.15.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String id;

    private final String resource;

    public ResourceNotFoundException(String id, String resource) {
        this.id = id;
        this.resource = resource;
    }

    public ResourceNotFoundException(String id, Class aClass) {
        this(id, aClass.getSimpleName().toLowerCase());
    }

    @Override
    public String getMessage() {
        return format("Resource '%s' with the given id '%s' not found", getResource(), getId());
    }

    public String getId() {
        return id;
    }

    public String getResource() {
        return resource;
    }
}
