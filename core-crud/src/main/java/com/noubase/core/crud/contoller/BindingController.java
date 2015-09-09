package com.noubase.core.crud.contoller;

import com.noubase.core.crud.domain.BindResource;
import com.noubase.core.crud.repository.ResourceBindingRepository;
import com.noubase.core.crud.validation.CreateResource;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by rshuper on 08.09.15.
 */
public abstract class BindingController<P extends Serializable, S extends Serializable, T extends BindResource<P, S>>
        extends CommonController<T, String> {

    protected final ResourceBindingRepository<P, S, T> repository;

    protected BindingController(
            final @NotNull Class<T> tClass,
            final @NotNull Class<? extends BindingController<P, S, T>> controllerClass,
            final ResourceBindingRepository<P, S, T> repository) {
        super(controllerClass, tClass);
        this.repository = repository;
    }

    @Override
    MongoRepository<T, String> mongoRepository() {
        return this.repository;
    }

    @NotNull
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> create(
            final @NotNull @Validated(CreateResource.class) @RequestBody T resource,
            final UriComponentsBuilder builder
    ) {
        return intCreate(resource, builder);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void remove(
            final @PathVariable String id
    ) {
        this.repository.delete(id);
    }

    @RequestMapping(value = "/{pid}", method = RequestMethod.DELETE)
    public void cleanUp(
            final @PathVariable P pid
    ) {
        Set<S> set = this.repository.findSecondaryByPrimary(pid);
    }

    @RequestMapping(value = "/primaries/{pid}", method = RequestMethod.DELETE)
    public void cleanUpPrimaries(
            final @PathVariable P pid
    ) {
    }

    @RequestMapping(value = "/batch", method = RequestMethod.DELETE)
    public void removeBatch(
            final @RequestBody List<T> list
    ) {

    }

    @RequestMapping(value = "/{pid}", method = RequestMethod.GET)
    public Set<P> getSecondaries(
            final @PathVariable P pid
    ) {
        return null;
    }

    @RequestMapping(value = "/primaries/{sid}", method = RequestMethod.GET)
    public Set<P> getPrimaries(
            final @PathVariable P sid
    ) {
        return null;
    }
}
