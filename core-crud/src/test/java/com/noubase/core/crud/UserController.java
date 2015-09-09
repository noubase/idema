package com.noubase.core.crud;

import com.noubase.core.crud.contoller.ResourceController;
import com.noubase.core.crud.model.relation.RelationsConfig;
import com.noubase.core.crud.model.relation.SecondaryRelationsConfig;
import com.noubase.core.crud.repository.ResourceBindingRepository;
import com.noubase.core.crud.repository.ResourceRepository;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rshuper on 18.08.15.
 */
@RestController
@RequestMapping(value = "/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController extends ResourceController<User, String> {

    @Autowired
    protected UserController(ResourceRepository<User, String> repo) {
        super(User.class, UserController.class, repo);
    }


    @Autowired
    private ResourceBindingRepository<ObjectId, String, GroupUser> bindingRepository;

    @Autowired
    private ResourceRepository<Group, ObjectId> groupRepository;

    @NotNull
    private User updatePassword(@NotNull User json) {
        json.setSalt(BCrypt.gensalt());
        json.setPassword(BCrypt.hashpw(json.getPassword(), json.getSalt()));
        return json;
    }

    @Override
    protected User doCreate(@NotNull User resource) {
        return super.doCreate(updatePassword(resource));
    }

    @NotNull
    @Override
    protected Set<RelationsConfig<String, ? extends Serializable>> relations() {
        Set<RelationsConfig<String, ? extends Serializable>> set = new HashSet<>();
        set.add(new SecondaryRelationsConfig<>("groups", bindingRepository, groupRepository));
        return set;
    }
}
