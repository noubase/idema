package com.noubase.core.crud;

import com.noubase.core.crud.contoller.BindingController;
import com.noubase.core.crud.repository.ResourceBindingRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rshuper on 08.09.15.
 */
@RestController
@RequestMapping(value = "/admin/group_user", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupUserController extends BindingController<ObjectId, String, GroupUser> {

    @Autowired
    protected GroupUserController(ResourceBindingRepository<ObjectId, String, GroupUser> repository) {
        super(GroupUser.class, GroupUserController.class, repository);
    }
}
