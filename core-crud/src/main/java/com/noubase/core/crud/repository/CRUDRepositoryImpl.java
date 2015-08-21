package com.noubase.core.crud.repository;

import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.noubase.core.crud.model.CollectionRequest;
import com.noubase.core.crud.model.ResourceRequest;
import com.noubase.core.crud.model.search.SearchRequest;
import com.noubase.core.crud.model.search.SearchType;
import com.noubase.core.util.AnnotationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.util.StringUtils.hasText;

/**
 * Created by rshuper on 11.08.15.
 */
public class CRUDRepositoryImpl<T extends Persistable<ID>, ID extends Serializable> extends SimpleMongoRepository<T, ID>
        implements CRUDRepository<T, ID>, PatchableRepository {

    @NotNull
    private final MongoOperations mongoOperations;
    @NotNull
    private final MongoEntityInformation<T, ID> metadata;

    private JsonPatcher jsonPatcher;

    @Override
    public void setJsonPatcher(@NotNull JsonPatcher jsonPatcher) {
        this.jsonPatcher = jsonPatcher;
    }

    public CRUDRepositoryImpl(@NotNull MongoEntityInformation<T, ID> metadata, @NotNull MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.metadata = metadata;
    }

    private Query idQuery(ID id) {
        return Query.query(where("id").is(id));
    }

    @NotNull
    private Query includeFields(@NotNull Query query, @NotNull Set<String> fields) {
        String[] strings = new String[fields.size()];
        fields.toArray(strings);
        return includeFields(query, strings);
    }

    @NotNull
    private Query booleanFields(@NotNull Query query, @NotNull Map<String, Boolean> fields) {
        if (!fields.isEmpty()) {
            for (Map.Entry<String, Boolean> field : fields.entrySet()) {
                query.addCriteria(where(field.getKey()).is(field.getValue()));
            }
        }
        return query;
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
        } else if (StringUtils.hasText(field)) {
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
    public Page<T> findAll(CollectionRequest<T> request) {
        SearchRequest search = request.getSearch();
        Query query = hasText(search.getQuery()) ? searchByType(search) : new Query();

        Query includeFields = includeFields(query.with(request), request.getFields());
        Query finalQuery = booleanFields(includeFields, request.getBooleans());
        List<T> list = mongoOperations.find(finalQuery, metadata.getJavaType());
        Long count = mongoOperations.count(finalQuery, metadata.getJavaType());

        return new PageImpl<>(list, request, count);
    }

    @Override
    public T findOne(ID id, @NotNull ResourceRequest<T> request) {
        return mongoOperations.findOne(includeFields(idQuery(id), request.getFields()), metadata.getJavaType());
    }

    @Override
    public T patch(@NotNull T resource, List<JsonPatchOperation> operations) throws JsonPatchException, BindException {
        return this.save(jsonPatcher.patch(resource, operations, metadata.getJavaType()));
    }
}
