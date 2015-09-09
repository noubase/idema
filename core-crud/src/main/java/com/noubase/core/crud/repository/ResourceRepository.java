package com.noubase.core.crud.repository;

import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.noubase.core.crud.model.CollectionRequest;
import com.noubase.core.crud.model.ResourceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.validation.BindException;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rshuper on 11.08.15.
 */
@NoRepositoryBean
public interface ResourceRepository<T extends Persistable<ID>, ID extends Serializable> extends MongoRepository<T, ID> {

    Page<T> findAll(CollectionRequest<T> request);

    T findOne(ID id, ResourceRequest request);

    T patch(T resource, List<JsonPatchOperation> operations) throws JsonPatchException, BindException;
}
