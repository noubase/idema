package com.noubase.idema.exception;

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

    public String getId() {
        return id;
    }

    public String getResource() {
        return resource;
    }
}
