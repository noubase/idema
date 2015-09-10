package com.noubase.core.crud.model.relation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by rshuper on 09.09.15.
 */
public interface RelationsConfig<P extends Serializable, S extends Serializable> {

    @NotNull
    Set<S> getIds(P id);

    Map<P, Collection<S>> getIds(Set<P> ids);

    @NotNull
    String getField();

    @Nullable
    Method getMethod();

    void setMethod(@NotNull Method method);

    Iterable<? extends Persistable<S>> getItems(Set<? extends Serializable> set, Set<String> fields);
}
