package com.noubase.core.crud;

import com.google.common.collect.ImmutableSet;
import com.noubase.core.crud.contoller.ResourceController;
import com.noubase.core.crud.model.relation.PrimaryRelationsConfig;
import com.noubase.core.crud.model.relation.RelationsConfig;
import com.noubase.core.crud.repository.ResourceBindingRepository;
import com.noubase.core.crud.repository.ResourceRepository;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rshuper on 18.08.15.
 */
@RestController
@RequestMapping(value = "/admin/groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupController extends ResourceController<Group, ObjectId> {

    @Autowired
    protected GroupController(ResourceRepository<Group, ObjectId> repo) {
        super(Group.class, GroupController.class, repo);
    }

    @Autowired
    private ResourceBindingRepository<ObjectId, String, GroupUser> bindingRepository;

    @Autowired
    private ResourceRepository<User, String> userRepository;

    @NotNull
    @Override
    protected ImmutableSet<RelationsConfig<ObjectId, ? extends Serializable>> relations() {
        Set<RelationsConfig<ObjectId, ? extends Serializable>> set = new HashSet<>();
        set.add(new PrimaryRelationsConfig<>("users", bindingRepository, userRepository));
        return ImmutableSet.copyOf(set);
    }

}
