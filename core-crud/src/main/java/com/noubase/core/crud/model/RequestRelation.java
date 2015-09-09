package com.noubase.core.crud.model;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * Created by rshuper on 09.09.15.
 */
public class RequestRelation {

    public static final String DELIMITER = ":";

    private final String name;

    private final Set<String> fields;

    public RequestRelation(String name, Set<String> fields) {
        this.name = name;
        this.fields = fields;
    }

    public static RequestRelation fromParameter(String parameter) {
        List<String> list = Splitter.on(DELIMITER).splitToList(parameter);
        return new RequestRelation(list.get(0), Sets.newHashSet(list.subList(1, list.size())));
    }

    public String getName() {
        return name;
    }

    public Set<String> getFields() {
        return fields;
    }
}
