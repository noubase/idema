package com.noubase.idema.repository;

import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.noubase.idema.model.CollectionRequest;
import com.noubase.idema.model.ResourceRequest;
import com.noubase.idema.model.search.SearchRequest;
import com.noubase.idema.model.search.SearchType;
import com.noubase.idema.service.JsonPatcher;
import com.noubase.idema.util.AnnotationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.validation.BindException;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.util.StringUtils.hasText;

/**
 * Created by rshuper on 11.08.15.
 */
public class CRUDRepositoryImpl<T extends Persistable<ID>, ID extends Serializable> extends SimpleMongoRepository<T, ID>
        implements CRUDRepository<T, ID> {

    @NotNull
    private final MongoOperations mongoOperations;
    @NotNull
    private final MongoEntityInformation<T, ID> metadata;

    private JsonPatcher jsonPatcher;

    @Autowired
    public void setJsonPatcher(JsonPatcher jsonPatcher) {
        this.jsonPatcher = jsonPatcher;
    }


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

    @NotNull
    private Pattern regex(SearchType type, String query) {
        return Pattern.compile(format(SearchType.PREFIX.equals(type) ? "^%s" : "%s", query), Pattern.CASE_INSENSITIVE);
    }

    @NotNull
    private Query regex(SearchRequest request) {
        String field = request.getField();
        Query query = new Query();
        if (SearchRequest.ALL_FIELDS.equals(field)) {
            Set<String> fields = AnnotationUtil.getFieldsByAnnotation(metadata.getJavaType(), TextIndexed.class);
            Set<Criteria> or = new HashSet<>();
            for (String f : fields) {
                or.add(where(f).regex(regex(request.getType(), request.getQuery())));
            }
            query.addCriteria(new Criteria().orOperator(or.toArray(new Criteria[or.size()])));
        } else if (hasText(field)) {
            query = new Query(where(field).regex(regex(request.getType(), request.getQuery())));
        }
        return query;
    }

    @NotNull
    private Query searchByType(@NotNull SearchRequest request) {
        Query query;
        switch (request.getType()) {
            case LIKE:
            case PREFIX:
                query = regex(request);
                break;
            default:
                TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(request.getQuery());
                query = TextQuery.queryText(criteria).sortByScore();
        }
        return query;
    }

    @Nullable
    @Override
    public Page<T> findAll(CollectionRequest request) {
        Query query;
        SearchRequest search = request.getSearch();
        if (!hasText(search.getQuery())) {
            query = new Query();
        } else {
            query = searchByType(search);
        }

        Query finalQuery = includeFields(query.with(request), request.getFields());
        List<T> list = mongoOperations.find(finalQuery, metadata.getJavaType());
        Long count = mongoOperations.count(finalQuery, metadata.getJavaType());

        return new PageImpl<>(list, request, count);
    }

    @Override
    public T findOne(ID id, @NotNull ResourceRequest request) {
        return mongoOperations.findOne(includeFields(idQuery(id), request.getFields()), metadata.getJavaType());
    }

    @Override
    public T patch(T resource, List<JsonPatchOperation> operations) throws JsonPatchException, BindException {
        return this.save(jsonPatcher.patch(resource, operations, metadata.getJavaType()));
    }
}
