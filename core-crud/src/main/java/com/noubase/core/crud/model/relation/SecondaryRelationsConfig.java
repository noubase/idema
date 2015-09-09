package com.noubase.core.crud.model.relation;

import com.noubase.core.crud.domain.BindResource;
import com.noubase.core.crud.repository.ResourceBindingRepository;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.repository.MongoRepository;

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

    private MongoRepository<T, P> mongoRepository;

    public SecondaryRelationsConfig(String field, ResourceBindingRepository<P, S, B> bindingRepository, MongoRepository<T, P> mongoRepository) {
        super(field, bindingRepository);
        this.mongoRepository = mongoRepository;
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
    public Iterable<T> getItems(Set<P> set) {
        return mongoRepository.findAll(set);
    }
}
