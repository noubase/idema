package com.noubase.core.crud.contoller;

import com.noubase.core.crud.model.ResourceRequest;
import com.noubase.core.crud.model.relation.RelationsConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rshuper on 09.09.15.
 */
class RelationsFetcher<T extends Persistable<ID>, ID extends Serializable> {

    public T fetchRelations(
            final T one,
            final Class<T> tClass,
            final ResourceRequest request,
            final Set<RelationsConfig<ID, ? extends Serializable>> configs
    ) {
        if (!configs.isEmpty() && !request.getRelated().isEmpty()) {
            Set<RelationsConfig<ID, ? extends Serializable>> set = getExisted(request, configs);
            for (RelationsConfig<ID, ? extends Serializable> config : set) {
                Set<? extends Serializable> ids = config.getIds(one.getId());
                System.out.println("IDS: " + ids);
                getSetter(tClass, config.getField()).invoke(one, config.getItems(ids));
            }
        }
        return one;
    }

    @NotNull
    private Method getSetter(final Class<T> tClass, String field) {
        Method writeMethod = BeanUtils.getPropertyDescriptor(tClass, field).getWriteMethod();
        if (writeMethod == null) {
            throw new RuntimeException("Given class has no proper setter for a relationship.");
        }
        return writeMethod;
    }

    @NotNull
    private Set<RelationsConfig<ID, ? extends Serializable>> getExisted(
            final ResourceRequest request,
            final Set<RelationsConfig<ID, ? extends Serializable>> configs
    ) {
        Set<RelationsConfig<ID, ? extends Serializable>> set = new HashSet<>();
        for (String field : request.getRelated()) {
            for (RelationsConfig<ID, ? extends Serializable> config : configs) {

                if (field.equalsIgnoreCase(config.getField())) {
                    set.add(config);
                }
            }
        }
        return set;
    }
}
