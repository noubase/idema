package com.noubase.core.crud.model.relation;

import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by rshuper on 09.09.15.
 */
public interface RelationsConfig<P extends Serializable, S extends Serializable> {

    Set<S> getIds(P id);

    String getField();

    Iterable<? extends Persistable<S>> getItems(Set<S> set);
}
