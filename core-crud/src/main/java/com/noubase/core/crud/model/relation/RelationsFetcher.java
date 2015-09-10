package com.noubase.core.crud.model.relation;

import com.noubase.core.crud.model.RequestRelation;
import com.noubase.core.crud.model.ResourceRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by rshuper on 09.09.15.
 */
public class RelationsFetcher<T extends Persistable<ID>, ID extends Serializable> {

    private final Set<RelationsConfig<ID, ? extends Serializable>> configs;

    private final Class<T> tClass;

    public RelationsFetcher(Set<RelationsConfig<ID, ? extends Serializable>> configs, Class<T> tClass) {
        this.configs = configs;
        this.tClass = tClass;
    }

    public T fetchRelations(
            final T one,
            final ResourceRequest request
    ) throws InvocationTargetException, IllegalAccessException {
        if (!configs.isEmpty() && !request.getRelated().isEmpty()) {
            Map<RequestRelation, RelationsConfig<ID, ? extends Serializable>> map = getExisted(request, configs);
            for (Map.Entry<RequestRelation, RelationsConfig<ID, ? extends Serializable>> entry : map.entrySet()) {
                RelationsConfig<ID, ? extends Serializable> config = entry.getValue();
                Set<? extends Serializable> ids = config.getIds(one.getId());
                if (config.getMethod() == null) {
                    config.setMethod(getSetter(tClass, config.getField()));
                }
                config.getMethod().invoke(one, config.getItems(ids, entry.getKey().getFields()));
            }
        }
        return one;
    }

    public Set<T> fetchRelations(
            final Page<T> page,
            final ResourceRequest request
    ) throws InvocationTargetException, IllegalAccessException {
        Set<T> all = new LinkedHashSet<>();
        if (!configs.isEmpty() && !request.getRelated().isEmpty()) {
            HashSet<ID> ids = new HashSet<>();
            for (T one : page) {
                ids.add(one.getId());
            }
            Map<RequestRelation, RelationsConfig<ID, ? extends Serializable>> map = getExisted(request, configs);
            for (Map.Entry<RequestRelation, RelationsConfig<ID, ? extends Serializable>> entry : map.entrySet()) {
                RelationsConfig<ID, ? extends Serializable> config = entry.getValue();
                if (config.getMethod() == null) {
                    config.setMethod(getSetter(tClass, config.getField()));
                }
                Map<ID, ? extends Collection<? extends Serializable>> idMap = config.getIds(ids);
                Set<Serializable> relatedIds = new HashSet<>();
                for (Collection<? extends Serializable> collection : idMap.values()) {
                    relatedIds.addAll(collection);
                }

                ItemsWrapper<ID> wrapper = new ItemsWrapper<>(config.getItems(relatedIds, entry.getKey().getFields()));

                for (T t : page) {
                    Collection<Persistable<? extends Serializable>> collection = wrapper.getCollection(t.getId(), idMap);
                    config.getMethod().invoke(t, collection);
                }
            }
        }
        for (T t : page) {
            all.add(t);
        }
        return all;
    }

    @NotNull
    private Method getSetter(final Class tClass, String field) {
        Method writeMethod = BeanUtils.getPropertyDescriptor(tClass, field).getWriteMethod();
        if (writeMethod == null || writeMethod.getModifiers() != Modifier.PUBLIC) {
            throw new RuntimeException("Given class has no proper setter for a relationship.");
        }

        Class<?>[] types = writeMethod.getParameterTypes();
        if (types.length > 1 || !types[0].isAssignableFrom(Collection.class)) {
            throw new RuntimeException("Given field has a wrong setter signature");
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

    private static class ItemsWrapper<ID extends Serializable> {

        private final Iterable<? extends Persistable<? extends Serializable>> items;

        private final Map<Serializable, Persistable<? extends Serializable>> map = new HashMap<>();

        public ItemsWrapper(Iterable<? extends Persistable<? extends Serializable>> items) {
            this.items = items;
        }

        public Collection<Persistable<? extends Serializable>> getCollection(Serializable id, Map<ID, ? extends Collection<? extends Serializable>> map) {
            HashSet<Persistable<? extends Serializable>> set = new HashSet<>();
            for (Map.Entry<ID, ? extends Collection<? extends Serializable>> entry : map.entrySet()) {
                if (entry.getKey().toString().equals(id.toString())) {
                    for (Serializable val : entry.getValue()) {
                        Persistable<? extends Serializable> item = getItem(val);
                        if (item != null) {
                            set.add(item);
                        }
                    }

                }
            }
            return set;
        }

        @Nullable
        public Persistable<? extends Serializable> getItem(Serializable id) {
            if (!map.containsKey(id)) {
                Persistable<? extends Serializable> r = null;
                for (Persistable<? extends Serializable> item : items) {
                    if (item.getId().toString().equals(id.toString())) {
                        r = item;
                        break;
                    }
                }
                map.put(id, r);
            }
            return map.get(id);
        }
    }
}
