package com.noubase.core.crud;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by rshuper on 08.09.15.
 */
public class GroupUserControllerTest extends AbstractIntegrationTest<User, String, GroupUser> {

    @Autowired
    private GroupUserRepository repository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public GroupUserControllerTest() {
        super(User.class, GroupUser.class, GroupUserController.class);
    }

    @Before
    public void before() throws Exception {
        userRepository.deleteAll();
        groupRepository.deleteAll();
        repository.deleteAll();
        authenticatedAs("super_admin", "ROLE_SUPER_ADMIN");
    }


    @Test
    public void testAddingToGroup() throws Exception {
        Group main = new Group("main");
        User user = new User("will_be_added", "1", "ROLE_USER");
        String groupId = getResourceId(createSuccess(getURI(GroupController.class), main));
        String userId = getResourceId(createSuccess(getURI(UserController.class), user));

        GroupUser bind = new GroupUser();
        bind.setPrimary(new ObjectId(groupId));
        bind.setSecondary(userId);

        createSuccess(getURI(), bind);
    }
}