package com.noubase.core.crud.contoller;

import com.github.fge.jsonpatch.JsonPatchOperation;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.noubase.core.crud.annotation.Unchangeable;
import com.noubase.core.crud.exception.DuplicateFieldException;
import com.noubase.core.crud.exception.ResourceNotFoundException;
import com.noubase.core.crud.exception.ResourcesNotFoundException;
import com.noubase.core.crud.model.CollectionRequest;
import com.noubase.core.crud.model.Pager;
import com.noubase.core.crud.model.ResourceRequest;
import com.noubase.core.crud.model.relation.RelationsConfig;
import com.noubase.core.crud.model.relation.RelationsFetcher;
import com.noubase.core.crud.repository.ResourceRepository;
import com.noubase.core.crud.util.DomainUtil;
import com.noubase.core.crud.validation.CreateResource;
import com.noubase.core.util.AnnotationUtil;
import org.hibernate.validator.constraints.NotEmpty;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rshuper on 23.07.15.
 */
public abstract class ResourceController<T extends Persistable<ID>, ID extends Serializable> extends CommonController<T, ID> {

    protected int maxCollectionSize;

    @Value("${crud.collections.max_size: 10}")
    public void setMaxCollectionSize(int value) {
        this.maxCollectionSize = value;
    }

    private final ResourceRepository<T, ID> repo;

    private RelationsFetcher<T, ID> fetcher;

    protected ResourceController(
            final @NotNull Class<T> tClass,
            final @NotNull Class<? extends ResourceController<T, ID>> controllerClass,
            final @NotNull ResourceRepository<T, ID> repo
    ) {
        super(controllerClass, tClass);
        this.repo = repo;
    }

    @PostConstruct
    private void init() {
        this.fetcher = new RelationsFetcher<>(relations(), tClass);
    }

    @Override
    MongoRepository<T, ID> mongoRepository() {
        return this.repo;
    }

    @NotNull
    protected ImmutableSet<RelationsConfig<ID, ? extends Serializable>> relations() {
        return new ImmutableSet.Builder<RelationsConfig<ID, ? extends Serializable>>().build();
    }

    @NotNull
    private T copyFields(@NotNull T entity, @NotNull T json) {
        Set<String> ignore = AnnotationUtil.getFieldsByAnnotation(tClass, Unchangeable.class);
        BeanUtils.copyProperties(json, entity, ignore.toArray(new String[ignore.size()]));
        return entity;
    }

    @NotNull
    private T findOr404(final ID id) {
        T one = this.repo.findOne(id);
        if (one == null) {
            throw new ResourceNotFoundException(id.toString(), tClass);
        }
        return one;
    }

    protected boolean canDelete(T resource) {
        return resource != null;
    }

    @NotNull
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    public Pager<T> listAll(@NotNull HttpServletRequest r) throws Exception {
        CollectionRequest<T> collectionRequest = new CollectionRequest<>(tClass, r, maxCollectionSize);
        Page<T> page = this.repo.findAll(collectionRequest);
        Set<T> all = fetcher.fetchRelations(page, collectionRequest);
        Pager<T> pager = new Pager<>(collectionRequest, page.getTotalElements(), all);
        logger.debug("findAll() found {} items", all.size());
        return pager;
    }


    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public T get(
            final @NotNull @PathVariable ID id,
            final HttpServletRequest request
    ) throws Exception {
        ResourceRequest resourceRequest = new ResourceRequest(request);
        T one = this.repo.findOne(id, resourceRequest);
        if (one == null) {
            throw new ResourceNotFoundException(id.toString(), tClass);
        }
        return fetcher.fetchRelations(one, resourceRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_VALUE})
    protected T update(
            final @NotNull @PathVariable ID id,
            final @NotNull @Validated @RequestBody T json
    ) throws Exception {
        try {
            logger.debug("update() of id#{} with body {}", id, json);
            logger.debug("T json is of type {}", json.getClass());
            T entity = findOr404(id);
            try {
                copyFields(entity, json);
            } catch (Exception e) {
                logger.warn("while copying properties", e);
                throw Throwables.propagate(e);
            }
            logger.debug("merge: {}, type: {}", entity, tClass.getClass());
            T updated = this.repo.save(entity);
            logger.info("updated: {}, type: {}", updated, tClass.getClass());
            return updated;
        } catch (DuplicateKeyException e) {
            logger.error("Cannot update {} with duplicated key", tClass);
            throw DuplicateFieldException.create(e, tClass);
        }
    }

    @NotNull
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(
            final @NotNull @NotEmpty @PathVariable ID id
    ) throws Exception {
        T one = findOr404(id);
        this.repo.delete(one);
        logger.info("delete() with body {} and type {}", one, one.getClass());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @NotNull
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(
            final @NotNull @NotEmpty @RequestBody Set<ID> ids
    ) {
        Iterable<T> all = this.repo.findAll(ids);
        Set<T> toDelete = new HashSet<>();
        for (T resource : all) {
            if (canDelete(resource)) {
                toDelete.add(resource);
            }
        }
        Sets.SetView<Object> difference = Sets.symmetricDifference(DomainUtil.extractId(toDelete), ids);
        if (difference.size() > 0) {
            throw new ResourcesNotFoundException(difference.immutableCopy(), tClass);
        }
        this.repo.delete(toDelete);
        logger.info("batch delete() ids {}", ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @NotNull
    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void>
    patch(
            final @PathVariable ID id,
            final @Valid @RequestBody List<JsonPatchOperation> operations,
            final UriComponentsBuilder builder
    ) throws Exception {
        try {
            T one = findOr404(id);
            HttpHeaders headers =
                    buildCreationHeaders(this.controllerClass, builder, this.repo.patch(one, operations).getId());
            return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
        } catch (DuplicateKeyException e) {
            logger.error("Cannot patch {} with duplicated key", tClass);
            throw DuplicateFieldException.create(e, tClass);
        }
    }

    @NotNull
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> create(
            final @NotNull @Validated(CreateResource.class) @RequestBody T resource,
            final UriComponentsBuilder builder
    ) {
        return intCreate(resource, builder);
    }
}