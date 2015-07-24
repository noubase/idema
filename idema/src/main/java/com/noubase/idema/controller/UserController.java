package com.noubase.idema.controller;

import com.noubase.idema.model.User;
import com.noubase.idema.validation.CreateResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by rshuper on 23.07.15.
 */
@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController extends CRUDController<User, String> {

    @Autowired
    protected UserController(MongoRepository<User, String> repo) {
        super(User.class, repo);
    }

    private User updatePassword(User json) {
        json.setSalt(BCrypt.gensalt());
        json.setPassword(BCrypt.hashpw(json.getPassword(), json.getSalt()));
        return json;
    }

    @Override
    public List<User> listAll() {
        return super.listAll();
    }

    @Override
    public User create(final @Validated(CreateResource.class) @RequestBody User json) {
        return super.create(updatePassword(json));
    }

    @Override
    public User get(@PathVariable String id) {
        return super.get(id);
    }

    @Override
    public User update(final @PathVariable String id, final @Validated @RequestBody User json) {
        return super.update(id, json);
    }

    @Override
    public ResponseEntity<Void> delete(@PathVariable String s) {
        return super.delete(s);
    }
}
