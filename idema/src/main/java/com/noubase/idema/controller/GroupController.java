package com.noubase.idema.controller;

import com.noubase.idema.domain.Group;
import com.noubase.idema.model.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by rshuper on 23.07.15.
 */
@RestController
@RequestMapping(value = "/groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupController extends CRUDController<Group, String> {

    @Autowired
    protected GroupController(MongoRepository<Group, String> repo) {
        super(Group.class, repo);
    }

    @Override
    public Pager<Group> listAll(HttpServletRequest r) {
        return super.listAll(r);
    }

    @Override
    @PreAuthorize(value = "hasRole(ROLE_SUPER_ADMIN)")
    public Group create(@RequestBody Group json) {
        return super.create(json);
    }

    @Override
    public Group get(@PathVariable String s) {
        return super.get(s);
    }

    @Override
    public Group update(@PathVariable String s, @RequestBody Group json) {
        return super.update(s, json);
    }

    @Override
    public ResponseEntity<Void> delete(@PathVariable String s) {
        return super.delete(s);
    }
}
