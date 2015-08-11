package com.noubase.idema.repository;

import com.noubase.idema.model.CollectionRequest;
import com.noubase.idema.model.ResourceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by rshuper on 11.08.15.
 */
public class CRUDRepositoryImpl<T extends Persistable<ID>, ID extends Serializable> extends SimpleMongoRepository<T, ID>
        implements CRUDRepository<T, ID> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> metadata;

    public CRUDRepositoryImpl(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.metadata = metadata;
    }

    private Query idQuery(ID id) {
        return query(where("id").is(id));
    }

    private Query includeFields(Query query, Set<String> fields) {
        String[] strings = new String[fields.size()];
        fields.toArray(strings);
        return includeFields(query, strings);
    }

    private Query includeFields(Query query, String... fields) {
        Assert.notNull(query, "Query cannot be null");
        Assert.notNull(fields, "Fields set cannot be null");

        for (String field : fields) {
            query.fields().include(field);
        }
        return query;
    }

    @Override
    public Page<T> findAll(CollectionRequest request) {
        Assert.notNull(request, "The given collection request must not be null!");
        return null;
    }

    @Override
    public T findOne(ID id, ResourceRequest request) {
        Assert.notNull(request, "The given resource request must not be null!");
        return mongoOperations.findOne(includeFields(idQuery(id), request.getFields()), metadata.getJavaType());
    }
}
