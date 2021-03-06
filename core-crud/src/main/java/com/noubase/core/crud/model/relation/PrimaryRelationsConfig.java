package com.noubase.core.crud.model.relation;

import com.noubase.core.crud.domain.BindResource;
import com.noubase.core.crud.repository.ResourceBindingRepository;
import com.noubase.core.crud.repository.ResourceRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.*;

/**
 * Created by rshuper on 09.09.15.
 */
public final class PrimaryRelationsConfig
        <P extends Serializable, S extends Serializable, B extends BindResource<P, S>, T extends Persistable<S>>
        extends AbstractRelationsConfig<P, S, B>
        implements RelationsConfig<P, S> {

    private ResourceRepository<T, S> resourceRepository;

    public PrimaryRelationsConfig(String field, ResourceBindingRepository<P, S, B> bindingRepository, ResourceRepository<T, S> resourceRepository) {
        super(field, bindingRepository);
        this.resourceRepository = resourceRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<T> getItems(Set<? extends Serializable> set, Set<String> fields) {
        return resourceRepository.findAll((Set<S>) set, fields);
    }

    @NotNull
    @Override
    public Set<S> getIds(P id) {
        return convertS(bindingRepository.findSecondaryByPrimary(id));
    }

    @Override
    public Map<P, Collection<S>> getIds(Set<P> ids) {
        Map<P, Collection<S>> map = new HashMap<>();
        for (B b : bindingRepository.findSecondaryByPrimaryIn(ids)) {
            P pri = b.getPrimary();
            if (!map.containsKey(pri)) {
                map.put(pri, new HashSet<>());
            }
            map.get(pri).add(b.getSecondary());
        }
        return map;
    }
}































