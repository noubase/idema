package com.noubase.core.crud;

import com.google.common.collect.Sets;
import com.noubase.core.crud.contoller.ResourceController;
import com.noubase.core.crud.model.CollectionRequest;
import com.noubase.core.crud.model.Pager;
import com.noubase.core.crud.repository.ResourceBindingRepository;
import com.noubase.core.crud.repository.ResourceRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping(value = "/{id}/users", method = RequestMethod.GET)
    public Pager<User> getUsers(
            final @PathVariable ObjectId id,
            final HttpServletRequest request
    ) {
        Group one = get(id, request);
        Set<String> secondaries = bindingRepository.findSecondaryByPrimary(one.getId());
        CollectionRequest<User, String> collectionRequest = new CollectionRequest<>(User.class, request, maxCollectionSize);
        collectionRequest.setIds(secondaries);
        Page<User> page = this.userRepository.findAll(collectionRequest);
        Set<User> all = Sets.newLinkedHashSet(page);
        return new Pager<>(collectionRequest, page.getTotalElements(), all);
    }
}
