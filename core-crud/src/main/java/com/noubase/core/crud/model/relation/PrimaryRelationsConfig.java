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
public class PrimaryRelationsConfig
        <P extends Serializable, S extends Serializable, B extends BindResource<P, S>, T extends Persistable<S>>
        extends AbstractRelationsConfig<P, S, B>
        implements RelationsConfig<P, S> {

    private MongoRepository<T, S> mongoRepository;

    public PrimaryRelationsConfig(String field, ResourceBindingRepository<P, S, B> bindingRepository, MongoRepository<T, S> mongoRepository) {
        super(field, bindingRepository);
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Iterable<T> getItems(Set<S> set) {
        return mongoRepository.findAll(set);
    }

    @Override
    public Set<S> getIds(P id) {
        Set<S> result = new HashSet<>();
        for (B b : bindingRepository.findSecondaryByPrimary(id)) {
            result.add(b.getSecondary());
        }
        return result;
    }
}































