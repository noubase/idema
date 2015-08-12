package com.noubase.idema.controller;

import com.noubase.idema.domain.User;
import com.noubase.idema.repository.CRUDRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rshuper on 23.07.15.
 */
@RestController
@RequestMapping(value = "/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController extends CRUDController<User, String> {

    @Autowired
    protected UserController(CRUDRepository<User, String> repo) {
        super(User.class, UserController.class, repo);
    }

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

}
