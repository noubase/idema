package com.noubase.core.crud.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by rshuper on 11.08.15.
 */
public class ResourceRequest<U> extends PageRequest {

    public static final String PARAM_FIELDS = "fields";

    private Set<String> fields;

    protected final Class<U> uClass;

    private final HttpServletRequest request;

    public ResourceRequest(Class<U> uClass, HttpServletRequest request) {
        this(uClass, request, 0, 1, new Sort(new Sort.Order("one")));
    }

    protected ResourceRequest(Class<U> uClass, HttpServletRequest request, int page, int size, Sort sort) {
        super(page, size, sort);

        Assert.notNull(request, "The given http request must not be null!");
        this.request = request;
        this.uClass = uClass;

    }

    public HttpServletRequest getRequest() {
        return request;
    }

    @NotNull
    public Set<String> getFields() {
        if (fields == null) {
            this.fields = new LinkedHashSet<>();
            String f = getRequest().getParameter(PARAM_FIELDS);
            if (StringUtils.hasText(f)) {
                Collections.addAll(this.fields, f.split("\\s*,\\s*"));
            }
        }
        return fields;
    }
}
