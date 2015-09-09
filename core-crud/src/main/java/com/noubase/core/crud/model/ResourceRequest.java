package com.noubase.core.crud.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by rshuper on 11.08.15.
 */
public class ResourceRequest extends PageRequest {

    public static final String PARAM_FIELDS = "_fields";
    public static final String PARAM_RELATED = "_related";

    private Set<String> fields;

    private Set<String> related;

    private final HttpServletRequest request;

    public ResourceRequest(HttpServletRequest request) {
        this(request, 0, 1, new Sort(new Sort.Order("one")));
    }

    protected ResourceRequest(HttpServletRequest request, int page, int size, Sort sort) {
        super(page, size, sort);

        Assert.notNull(request, "The given http request must not be null!");
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    @NotNull
    public Set<String> getFields() {
        if (fields == null) {
            this.fields = new LinkedHashSet<>();
            populate(this.fields, PARAM_FIELDS);
        }
        return fields;
    }

    @NotNull
    public Set<String> getRelated() {
        if (related == null) {
            this.related = new LinkedHashSet<>();
            populate(this.related, PARAM_RELATED);
        }
        return related;
    }

    protected void populate(Collection<String> collection, String param) {
        String f = getRequest().getParameter(param);
        if (StringUtils.hasText(f)) {
            Collections.addAll(collection, f.split("\\s*,\\s*"));
        }
    }
}
