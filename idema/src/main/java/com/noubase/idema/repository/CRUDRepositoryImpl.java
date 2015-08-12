package com.noubase.idema.repository;

import com.noubase.idema.model.CollectionRequest;
import com.noubase.idema.model.ResourceRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by rshuper on 11.08.15.
 */
public class CRUDRepositoryImpl<T extends Persistable<ID>, ID extends Serializable> extends SimpleMongoRepository<T, ID>
        implements CRUDRepository<T, ID> {

    @NotNull
    private final MongoOperations mongoOperations;
    @NotNull
    private final MongoEntityInformation<T, ID> metadata;

    public CRUDRepositoryImpl(@NotNull MongoEntityInformation<T, ID> metadata, @NotNull MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.metadata = metadata;
    }

    private Query idQuery(ID id) {
        return query(where("id").is(id));
    }

    @NotNull
    private Query includeFields(@NotNull Query query, @NotNull Set<String> fields) {
        String[] strings = new String[fields.size()];
        fields.toArray(strings);
        return includeFields(query, strings);
    }

    @NotNull
    private Query includeFields(@NotNull Query query, @NotNull String... fields) {

        for (String field : fields) {
            query.fields().include(field);
        }
        return query;
    }

    @Nullable
    @Override
    public Page<T> findAll(CollectionRequest request) {

        Query query = includeFields(new Query().with(request), request.getFields());
        List<T> list = mongoOperations.find(query, metadata.getJavaType());
        Long count = mongoOperations.count(query, metadata.getJavaType());

        return new PageImpl<>(list, request, count);
    }

    @Override
    public T findOne(ID id, @NotNull ResourceRequest request) {
        return mongoOperations.findOne(includeFields(idQuery(id), request.getFields()), metadata.getJavaType());
    }
}
