package com.noubase.core.crud.model.relation;

import com.noubase.core.crud.domain.BindResource;
import com.noubase.core.crud.repository.ResourceBindingRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rshuper on 09.09.15.
 */
abstract class AbstractRelationsConfig<P extends Serializable, S extends Serializable, B extends BindResource<P, S>> {

    private final String field;

    protected final ResourceBindingRepository<P, S, B> bindingRepository;

    private Method method;

    @Nullable
    public Method getMethod() {
        return method;
    }

    public void setMethod(@NotNull Method method) {
        this.method = method;
    }

    @NotNull
    public String getField() {
        return field;
    }

    public AbstractRelationsConfig(String field, ResourceBindingRepository<P, S, B> bindingRepository) {
        this.field = field;
        this.bindingRepository = bindingRepository;
    }

    protected Set<P> convertP(Set<B> bs) {
        Set<P> result = new HashSet<>();
        for (B b : bs) {
            result.add(b.getPrimary());
        }
        return result;
    }

    protected Set<S> convertS(Set<B> bs) {
        Set<S> result = new HashSet<>();
        for (B b : bs) {
            result.add(b.getSecondary());
        }
        return result;
    }
}
