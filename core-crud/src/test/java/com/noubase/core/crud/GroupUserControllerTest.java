package com.noubase.core.crud;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by rshuper on 08.09.15.
 */
public class GroupUserControllerTest extends AbstractIntegrationTest<User, String, GroupUser> {

    @Autowired
    private GroupUserRepository repository;

    protected GroupUserControllerTest() {
        super(User.class, GroupUser.class, GroupUserController.class);
    }

    @Before
    public void before() throws Exception {
        repository.deleteAll();
        authenticatedAs("super_admin", "ROLE_SUPER_ADMIN");
    }


}