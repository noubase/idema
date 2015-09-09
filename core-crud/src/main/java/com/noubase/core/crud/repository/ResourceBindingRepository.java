package com.noubase.core.crud.repository;

import com.noubase.core.crud.domain.BindResource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by rshuper on 07.09.15.
 */
@NoRepositoryBean
public interface ResourceBindingRepository<P extends Serializable, S extends Serializable, T extends BindResource<P, S>> extends MongoRepository<T, String> {

    Set<T> findPrimaryBySecondary(S id);

    Set<T> findSecondaryByPrimary(P id);
}
