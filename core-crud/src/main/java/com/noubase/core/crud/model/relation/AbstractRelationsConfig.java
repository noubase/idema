package com.noubase.core.crud.model.relation;

import com.noubase.core.crud.domain.BindResource;
import com.noubase.core.crud.repository.ResourceBindingRepository;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

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

    protected Query buildQuery(Set<? extends Serializable> set, Set<String> fields) {
        Query query = query(where(Fields.UNDERSCORE_ID).in(set));
        if (!fields.isEmpty()) {
            for (String f : fields) {
                query.fields().include(f);
            }
        }
        return query;
    }

}
