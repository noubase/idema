package com.noubase.core.crud;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.noubase.core.crud.model.ResourceRequest.PARAM_RELATED;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

        String groupURL = format("%s/%s?%s=users:id:username", getURI(GroupController.class), groupId, PARAM_RELATED);
        String userURL = format("%s/%s?%s=groups:slug", getURI(UserController.class), userId, PARAM_RELATED);

        getSuccess(groupURL).
                andDo(print())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users", hasSize(1)))
                .andExpect(jsonPath("$.users[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$.users[0].firstName").doesNotExist())
                .andExpect(jsonPath("$.groups[0].created").doesNotExist())
        ;
        getSuccess(userURL).
                andDo(print())
                .andExpect(jsonPath("$.groups").isArray())
                .andExpect(jsonPath("$.groups", hasSize(1)))
                .andExpect(jsonPath("$.groups[0].slug", is(main.getSlug())))
                .andExpect(jsonPath("$.groups[0].modified").doesNotExist())
                .andExpect(jsonPath("$.groups[0].created").doesNotExist())
        ;
    }
}