package com.noubase.idema.controller;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.noubase.idema.annotation.Unchangeable;
import com.noubase.idema.exception.DuplicateFieldException;
import com.noubase.idema.exception.ResourceNotFoundException;
import com.noubase.idema.model.CollectionRequest;
import com.noubase.idema.model.Headers;
import com.noubase.idema.model.Pager;
import com.noubase.idema.model.ResourceRequest;
import com.noubase.idema.repository.CRUDRepository;
import com.noubase.idema.validation.CreateResource;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Persistable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.noubase.idema.util.AnnotationUtil.getFieldsByAnnotation;
import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * Created by rshuper on 23.07.15.
 */
public abstract class CRUDController<T extends Persistable<ID>, ID extends Serializable> {

    private int maxCollectionSize;

    @Value("${crud.collections.max_size ?: 10}")
    public void setMaxCollectionSize(int value) {
        this.maxCollectionSize = value; // todo: investigate
    }

    private final Logger logger;
    private final CRUDRepository<T, ID> repo;
    private final Class<T> tClass;
    private final Class<? extends CRUDController<T, ID>> controllerClass;

    protected CRUDController(Class<T> tClass, Class<? extends CRUDController<T, ID>> controllerClass, CRUDRepository<T, ID> repo) {
        this.tClass = tClass;
        this.controllerClass = controllerClass;
        this.repo = repo;
        this.logger = LoggerFactory.getLogger(controllerClass);
    }

    private T copyFields(T entity, T json) {
        Set<String> ignore = getFieldsByAnnotation(tClass, Unchangeable.class);
        copyProperties(json, entity, ignore.toArray(new String[ignore.size()]));
        return entity;
    }

    private HttpHeaders buildCreationHeaders(Class controller, UriComponentsBuilder builder, Set<ID> ids, Map<String, Object> variables) {
        HttpHeaders headers = new HttpHeaders();

        if (ids.size() == 1 && builder != null) {
            RequestMapping mapping = (RequestMapping) controller.getAnnotation(RequestMapping.class);
            if (mapping == null) {
                throw new RuntimeException("Cannot handle resource creation automatically.");
            }
            variables.put("id", ids.iterator().next());
            UriComponents uriComponents =
                    builder.path(mapping.value()[0] + "/{id}").buildAndExpand(variables);
            headers.setLocation(uriComponents.toUri());
        }

        headers.set(Headers.RESOURCE_ID, Joiner.on(",").join(ids));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders buildCreationHeaders(Class controller, UriComponentsBuilder builder, Set<ID> ids) {
        return buildCreationHeaders(controller, builder, ids, new HashMap<>());
    }

    private HttpHeaders buildCreationHeaders(Class controller, UriComponentsBuilder builder, ID id) {
        HashSet<ID> set = new HashSet<>();
        set.add(id);
        return buildCreationHeaders(controller, builder, set, new HashMap<>());
    }

    protected T doCreate(T resource) {
        return this.repo.save(resource);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    public Pager<T> listAll(HttpServletRequest r) {
        CollectionRequest collectionRequest = new CollectionRequest(r, maxCollectionSize);
        Set<T> all = Sets.newLinkedHashSet(this.repo.findAll(collectionRequest));
        Pager<T> pager = new Pager<>(collectionRequest, this.repo.count(), all);
        logger.debug("findAll() found {} items", all.size());
        return pager;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> create(
            final @Validated(CreateResource.class) @RequestBody T resource,
            final UriComponentsBuilder builder
    ) {
        try {
            logger.debug("create() with body {} of type {}", resource, resource.getClass());
            T one = doCreate(resource);
            HttpHeaders headers = buildCreationHeaders(this.controllerClass, builder, one.getId());
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            logger.error("Cannot create {} with duplicated key", tClass);
            throw DuplicateFieldException.create(e, tClass);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/batch", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    public ResponseEntity<Void> batch(
            final @Validated(CreateResource.class) @RequestBody Set<T> resources,
            final UriComponentsBuilder builder
    ) {
        try {
            Set<ID> ids = new HashSet<>();
            for (T resource : resources) {
                T one = doCreate(resource);
                ids.add(one.getId());
            }
            HttpHeaders headers = buildCreationHeaders(this.controllerClass, builder, ids);
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            logger.error("Cannot create {} with duplicated key", tClass);
            throw DuplicateFieldException.create(e, tClass);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public T get(
            final @PathVariable ID id,
            final HttpServletRequest request
    ) {
        T one = this.repo.findOne(id, new ResourceRequest(request));
        if (one == null) {
            throw new ResourceNotFoundException(id.toString(), tClass.getSimpleName().toLowerCase());
        }
        return one;
    }

    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_VALUE})
    protected T update(
            final @PathVariable ID id,
            final @Validated @RequestBody T json,
            final HttpServletRequest request
    ) throws DuplicateFieldException {
        try {
            logger.debug("update() of id#{} with body {}", id, json);
            logger.debug("T json is of type {}", json.getClass());
            T entity = get(id, request);
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

    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(
            final @NotEmpty @PathVariable ID id,
            final HttpServletRequest request
    ) {
        T one = get(id, request);
        this.repo.delete(one);
        logger.info("delete() with body {} and type {}", one, one.getClass());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}