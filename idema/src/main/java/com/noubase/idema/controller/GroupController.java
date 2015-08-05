package com.noubase.idema.controller;

import com.noubase.idema.domain.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rshuper on 23.07.15.
 */
@RestController
@RequestMapping(value = "/admin/groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupController extends CRUDController<Group, String> {

    @Autowired
    protected GroupController(MongoRepository<Group, String> repo) {
        super(Group.class, repo);
    }
}
