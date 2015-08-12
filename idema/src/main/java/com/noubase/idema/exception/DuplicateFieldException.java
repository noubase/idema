package com.noubase.idema.exception;

import com.noubase.idema.util.MongoUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class DuplicateFieldException extends RuntimeException {

    private final String field;

    private final String resource;

    public DuplicateFieldException(String message, String field, String resource) {
        super(message);
        this.field = field;
        this.resource = resource;
    }

    @NotNull
    public static DuplicateFieldException create(@NotNull Throwable e, @NotNull Class aClass) {
        Assert.notNull(e);
        Assert.notNull(aClass);

        String field = MongoUtil.extractDuplicatedField(e.getMessage());
        return new DuplicateFieldException(field + " should be unique", field, aClass.getSimpleName().toLowerCase());
    }

    public String getField() {
        return field;
    }

    public String getResource() {
        return resource;
    }
}
