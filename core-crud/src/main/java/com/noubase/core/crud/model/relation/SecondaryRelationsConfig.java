package com.noubase.core.crud.model.relation;

import com.noubase.core.crud.domain.BindResource;
import com.noubase.core.crud.repository.ResourceBindingRepository;
import com.noubase.core.crud.repository.ResourceRepository;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rshuper on 09.09.15.
 */
public class SecondaryRelationsConfig
        <P extends Serializable, S extends Serializable, B extends BindResource<P, S>, T extends Persistable<P>>
        extends AbstractRelationsConfig<P, S, B>
        implements RelationsConfig<S, P> {

    private ResourceRepository<T, P> resourceRepository;

    public SecondaryRelationsConfig(String field, ResourceBindingRepository<P, S, B> bindingRepository, ResourceRepository<T, P> mongoRepository) {
        super(field, bindingRepository);
        this.resourceRepository = mongoRepository;
    }

    @Override
    public Set<P> getIds(S id) {
        Set<P> result = new HashSet<>();
        for (B b : bindingRepository.findPrimaryBySecondary(id)) {
            result.add(b.getPrimary());
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<T> getItems(Set<? extends Serializable> set, Set<String> fields) {
        return resourceRepository.findAll((Set<P>) set, fields);
    }
}
