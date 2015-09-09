package com.noubase.core.crud.model.relation;

import com.noubase.core.crud.domain.BindResource;
import com.noubase.core.crud.repository.ResourceBindingRepository;

import java.io.Serializable;

/**
 * Created by rshuper on 09.09.15.
 */
abstract class AbstractRelationsConfig<P extends Serializable, S extends Serializable, B extends BindResource<P, S>> {

    protected final String field;

    protected final ResourceBindingRepository<P, S, B> bindingRepository;

    public String getField() {
        return field;
    }

    public AbstractRelationsConfig(String field, ResourceBindingRepository<P, S, B> bindingRepository) {
        this.field = field;
        this.bindingRepository = bindingRepository;
    }


}
