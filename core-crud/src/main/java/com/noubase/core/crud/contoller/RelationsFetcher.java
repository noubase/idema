package com.noubase.core.crud.contoller;

import com.noubase.core.crud.model.RequestRelation;
import com.noubase.core.crud.model.ResourceRequest;
import com.noubase.core.crud.model.relation.RelationsConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
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
    ) throws InvocationTargetException, IllegalAccessException {
        if (!configs.isEmpty() && !request.getRelated().isEmpty()) {
            Map<RequestRelation, RelationsConfig<ID, ? extends Serializable>> map = getExisted(request, configs);
            for (Map.Entry<RequestRelation, RelationsConfig<ID, ? extends Serializable>> entry : map.entrySet()) {
                RelationsConfig<ID, ? extends Serializable> config = entry.getValue();
                Set<? extends Serializable> ids = config.getIds(one.getId());
                getSetter(tClass, config.getField()).invoke(one, config.getItems(ids, entry.getKey().getFields()));
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
    private Map<RequestRelation, RelationsConfig<ID, ? extends Serializable>> getExisted(
            final ResourceRequest request,
            final Set<RelationsConfig<ID, ? extends Serializable>> configs
    ) {
        Map<RequestRelation, RelationsConfig<ID, ? extends Serializable>> map = new HashMap<>();
        for (RequestRelation rel : request.getRelated()) {
            for (RelationsConfig<ID, ? extends Serializable> config : configs) {

                if (rel.getName().equalsIgnoreCase(config.getField())) {
                    map.put(rel, config);
                }
            }
        }
        return map;
    }
}
