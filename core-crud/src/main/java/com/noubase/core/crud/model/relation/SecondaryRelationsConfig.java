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
public final class SecondaryRelationsConfig
        <P extends Serializable, S extends Serializable, B extends BindResource<P, S>, T extends Persistable<P>>
        extends AbstractRelationsConfig<P, S, B>
        implements RelationsConfig<S, P> {

    private ResourceRepository<T, P> resourceRepository;

    public SecondaryRelationsConfig(String field, ResourceBindingRepository<P, S, B> bindingRepository, ResourceRepository<T, P> mongoRepository) {
        super(field, bindingRepository);
        this.resourceRepository = mongoRepository;
    }

    @NotNull
    @Override
    public Set<P> getIds(S id) {
        return convertP(bindingRepository.findPrimaryBySecondary(id));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<T> getItems(Set<? extends Serializable> set, Set<String> fields) {
        return resourceRepository.findAll((Set<P>) set, fields);
    }

    @Override
    public Map<S, Collection<P>> getIds(Set<S> ids) {
        Map<S, Collection<P>> map = new HashMap<>();
        for (B b : bindingRepository.findPrimaryBySecondaryIn(ids)) {
            S sec = b.getSecondary();
            if (!map.containsKey(sec)) {
                map.put(sec, new HashSet<>());
            }
            map.get(sec).add(b.getPrimary());
        }
        return map;
    }
}
