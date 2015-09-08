package com.noubase.core.crud;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by rshuper on 08.09.15.
 */
public class GroupControllerTest extends AbstractIntegrationTest<User, ObjectId, Group> {

    @Autowired
    private GroupRepository repository;

    protected GroupControllerTest() {
        super(User.class, Group.class, GroupController.class);
    }

    @Before
    public void before() throws Exception {
        repository.deleteAll();
        authenticatedAs("super_admin", "ROLE_SUPER_ADMIN");
    }

    @Test
    public void fullCycle() throws Exception {
        Group main = createAndConvert(new Group("main"));
        Group another = createAndConvert(new Group("another"));
    }
}