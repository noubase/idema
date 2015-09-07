package com.noubase.core.crud;

import com.noubase.core.crud.contoller.ResourceController;
import com.noubase.core.crud.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rshuper on 18.08.15.
 */
@RestController
@RequestMapping(value = "/admin/groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupController extends ResourceController<Group, String> {

    @Autowired
    protected GroupController(ResourceRepository<Group, String> repo) {
        super(Group.class, GroupController.class, repo);
    }
}
