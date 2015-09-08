package com.noubase.core.crud.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.io.Serializable;

/**
 * Created by rshuper on 13.08.15.
 */
public class ResourceMongoRepositoryFactoryBean<T extends MongoRepository<S, ID>, S extends Persistable<ID>, ID extends Serializable>
        extends MongoRepositoryFactoryBean<T, S, ID> {

    @Autowired
    @SuppressWarnings({"SpringJavaAutowiringInspection", "SpringJavaAutowiredMembersInspection"})
    private JsonPatcher patcher;

    @Override
    public T getObject() {
        T t = super.getObject();
        if (t instanceof PatchableRepository) {
            ((PatchableRepository) t).setJsonPatcher(patcher);
        }
        return t;
    }


    @Override
    protected RepositoryFactorySupport getFactoryInstance(MongoOperations operations) {
        RepositoryFactorySupport support = super.getFactoryInstance(operations);
        support.addRepositoryProxyPostProcessor(new ProxyPostProcessor());
        return support;
    }

}
