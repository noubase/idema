package com.noubase.idema.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;

import static java.lang.String.format;

/**
 * Created by rshuper on 23.07.15.
 */
public class ResourcesNotFoundException extends ResourceNotFoundException {

    private final Set<Object> ids;

    public ResourcesNotFoundException(Set<Object> ids, String resource) {
        super(null, resource);
        this.ids = ids;
    }

    public ResourcesNotFoundException(Set<Object> ids, Class aClass) {
        super(null, aClass);
        this.ids = ids;
    }

    @Override
    public String getMessage() {
        return format("Resources '%s' with the given ids '%s' not found", getResource(), getIds());
    }

    @JsonIgnore
    public String getId() {
        return super.getId();
    }

    public Set<Object> getIds() {
        return ids;
    }
}
