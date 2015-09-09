package com.noubase.core.crud.contoller;

import com.google.common.base.Joiner;
import com.noubase.core.crud.exception.DuplicateFieldException;
import com.noubase.core.crud.model.Headers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by rshuper on 08.09.15.
 */
abstract class CommonController<T extends Persistable<ID>, ID extends Serializable> {

    protected final Logger logger;
    protected final Class controllerClass;
    protected final Class<T> tClass;

    CommonController(Class<? extends CommonController<T, ID>> controllerClass, Class<T> tClass) {
        this.logger = LoggerFactory.getLogger(controllerClass);
        this.controllerClass = controllerClass;
        this.tClass = tClass;
    }

    protected T doCreate(T resource) {
        return mongoRepository().save(resource);
    }

    abstract MongoRepository<T, ID> mongoRepository();

    @NotNull
    HttpHeaders buildCreationHeaders(@NotNull Class controller, @Nullable UriComponentsBuilder builder, @NotNull Set<ID> ids, @NotNull Map<String, Object> variables) {
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

    @NotNull
    HttpHeaders buildCreationHeaders(@NotNull Class controller, UriComponentsBuilder builder, ID id) {
        HashSet<ID> set = new HashSet<>();
        set.add(id);
        return buildCreationHeaders(controller, builder, set, new HashMap<>());
    }

    ResponseEntity<Void> intCreate(final T resource, final UriComponentsBuilder builder) {
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
}
