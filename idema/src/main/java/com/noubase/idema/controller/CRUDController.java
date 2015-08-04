package com.noubase.idema.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.noubase.idema.annotation.Unchangeable;
import com.noubase.idema.exception.DuplicateFieldException;
import com.noubase.idema.exception.ResourceNotFoundException;
import com.noubase.idema.model.CollectionRequest;
import com.noubase.idema.model.Pager;
import com.noubase.idema.serialization.Public;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static com.noubase.idema.util.AnnotationUtil.getFieldsByAnnotation;
import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * Created by rshuper on 23.07.15.
 */
abstract class CRUDController<T, ID extends Serializable> {

    private Logger logger = LoggerFactory.getLogger(CRUDController.class);
    private MongoRepository<T, ID> repo;
    private Class<T> tClass;

    protected CRUDController(Class<T> tClass, MongoRepository<T, ID> repo) {
        this.tClass = tClass;
        this.repo = repo;
    }

    private T copyFields(T entity, T json) {
        Set<String> ignore = getFieldsByAnnotation(tClass, Unchangeable.class);
        copyProperties(json, entity, ignore.toArray(new String[ignore.size()]));
        return entity;
    }

    @ResponseBody
    @JsonView(Public.class)
    @RequestMapping(method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    public Pager<T> listAll(HttpServletRequest r) {
        CollectionRequest collectionRequest = new CollectionRequest(r);
        HashSet<T> all = Sets.newHashSet(this.repo.findAll(collectionRequest));
        Pager<T> pager = new Pager<>(collectionRequest, this.repo.count(), all);
        logger.debug("findAll() found {} items", all.size());
        return pager;
    }

    @ResponseBody
    @JsonView(Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public T create(T json) {
        try {
            logger.debug("create() with body {} of type {}", json, json.getClass());
            return this.repo.save(json);
        } catch (DuplicateKeyException e) {
            logger.error("Cannot create {} with duplicated key", tClass);
            throw DuplicateFieldException.create(e, tClass);
        }
    }

    @ResponseBody
    @JsonView(Public.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public T get(final @PathVariable ID id) {
        T one = this.repo.findOne(id);
        if (one == null) {
            throw new ResourceNotFoundException(id.toString(), tClass.getSimpleName().toLowerCase());
        }
        return one;
    }

    @ResponseBody
    @JsonView(Public.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_VALUE})
    protected T update(final @PathVariable ID id, T json) throws DuplicateFieldException {
        try {
            logger.debug("update() of id#{} with body {}", id, json);
            logger.debug("T json is of type {}", json.getClass());
            T entity = get(id);
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
    @JsonView(Public.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(final @NotEmpty @PathVariable ID id) {
        T one = get(id);
        this.repo.delete(one);
        logger.info("delete() with body {} and type {}", one, one.getClass());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}