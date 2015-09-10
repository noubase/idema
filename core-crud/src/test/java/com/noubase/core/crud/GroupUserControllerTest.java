package com.noubase.core.crud;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.noubase.core.crud.model.ResourceRequest.PARAM_RELATED;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isOneOf;
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
    public void testRelationsForOne() throws Exception {
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

    @Test
    public void testRelationsForCollection() throws Exception {
        Group one = new Group("one");
        Group two = new Group("two");
        Group three = new Group("three");

        User user = new User("simpler_user", "1", "ROLE_USER");
        User user2 = new User("user2", "1", "ROLE_USER");

        String oneId = getResourceId(createSuccess(getURI(GroupController.class), one));
        String twoId = getResourceId(createSuccess(getURI(GroupController.class), two));
        String threeId = getResourceId(createSuccess(getURI(GroupController.class), three));

        String userId = getResourceId(createSuccess(getURI(UserController.class), user));
        String user2Id = getResourceId(createSuccess(getURI(UserController.class), user2));

        GroupUser bind = new GroupUser();
        bind.setPrimary(new ObjectId(oneId));
        bind.setSecondary(userId);

        GroupUser bind2 = new GroupUser();
        bind2.setPrimary(new ObjectId(twoId));
        bind2.setSecondary(userId);

        GroupUser bind3 = new GroupUser();
        bind3.setPrimary(new ObjectId(threeId));
        bind3.setSecondary(user2Id);

        createSuccess(getURI(), bind);
        createSuccess(getURI(), bind2);
        createSuccess(getURI(), bind3);

        String groupURL = format("%s?%s=users:username", getURI(GroupController.class), PARAM_RELATED);
        String userURL = format("%s?%s=groups:slug", getURI(UserController.class), PARAM_RELATED);

        getSuccess(groupURL)
                .andExpect(jsonPath("$.items[0].slug", is(three.getSlug())))
                .andExpect(jsonPath("$.items[0].users").isArray())
                .andExpect(jsonPath("$.items[0].users", hasSize(1)))
                .andExpect(jsonPath("$.items[0].users[0].username", is(user2.getUsername())))
                .andExpect(jsonPath("$.items[0].users[0].created").doesNotExist())

                .andExpect(jsonPath("$.items[1].slug", is(two.getSlug())))
                .andExpect(jsonPath("$.items[1].users").isArray())
                .andExpect(jsonPath("$.items[1].users", hasSize(1)))
                .andExpect(jsonPath("$.items[1].users[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$.items[1].users[1].created").doesNotExist())

                .andExpect(jsonPath("$.items[2].slug", is(one.getSlug())))
                .andExpect(jsonPath("$.items[2].users").isArray())
                .andExpect(jsonPath("$.items[2].users", hasSize(1)))
                .andExpect(jsonPath("$.items[2].users[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$.items[2].users[1].modified").doesNotExist())

        ;
        getSuccess(userURL)
                .andExpect(jsonPath("$.items[0].username", is(user2.getUsername())))
                .andExpect(jsonPath("$.items[0].groups").isArray())
                .andExpect(jsonPath("$.items[0].groups", hasSize(1)))
                .andExpect(jsonPath("$.items[0].groups[0].slug", is(three.getSlug())))
                .andExpect(jsonPath("$.items[0].groups[0].created").doesNotExist())

                .andExpect(jsonPath("$.items[1].username", is(user.getUsername())))
                .andExpect(jsonPath("$.items[1].groups").isArray())
                .andExpect(jsonPath("$.items[1].groups", hasSize(2)))
                .andExpect(jsonPath("$.items[1].groups[0].slug", isOneOf(two.getSlug(), one.getSlug())))
                .andExpect(jsonPath("$.items[1].groups[1].slug", isOneOf(two.getSlug(), one.getSlug())))
                .andExpect(jsonPath("$.items[1].groups[0].created").doesNotExist())
        ;
    }
}