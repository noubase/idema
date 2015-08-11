package com.noubase.idema.repository;

import com.noubase.idema.model.CollectionRequest;
import com.noubase.idema.model.ResourceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by rshuper on 11.08.15.
 */
@NoRepositoryBean
public interface CRUDRepository<T extends Persistable<ID>, ID extends Serializable> extends MongoRepository<T, ID> {

    Page<T> findAll(CollectionRequest request);

    T findOne(ID id, ResourceRequest request);
}
