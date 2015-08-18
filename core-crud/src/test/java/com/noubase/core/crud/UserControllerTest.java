package com.noubase.core.crud;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.noubase.core.crud.model.CollectionRequest.*;
import static com.noubase.core.crud.test.TestUtil.convertTo;
import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rshuper on 18.08.15.
 */
public class UserControllerTest extends AbstractIntegrationTest<User> {

    @Autowired
    private UserRepository repository;

    public UserControllerTest() {
        super(User.class);
    }

    private String getURI() {
        return getURI(UserController.class);
    }

    private String getURI(@NotNull User user) {
        return format("%s/%s", getURI(), user.getId());
    }

    private User createAndConvert(User user) throws Exception {
        String location = getLocation(createSuccess(this.getURI(), user));
        return convertTo(getSuccess(location), User.class);
    }

    @Before
    public void before() throws Exception {
        repository.deleteAll();
        authenticatedAs("super_admin", "ROLE_SUPER_ADMIN");
    }

    @Test
    public void testWithBadRole() throws Exception {
        authenticatedAs("user", "USER");
        get(getURI()).andExpect(status().isBadRequest());
    }

    @NotNull
    private User user(String username) {
        return new User(username, "12345678", "ROLE_USER");
    }


    /////////////////////////////////
    //////// READ COLLECTION ////////
    /////////////////////////////////


    @Test
    public void testListAllPaging() throws Exception {
        User u1 = user("a_listAll");
        User u2 = user("z_listAll");
        createSuccess(getURI(), u1);
        createSuccess(getURI(), u2);
        getSuccess(getURI())
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.page", is(DEFAULT_PAGE)))
                .andExpect(jsonPath("$.pages", is(1)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.size", is(DEFAULT_SIZE)))
                .andExpect(jsonPath("$.next").doesNotExist())
                .andExpect(jsonPath("$.prev").doesNotExist())
        ;

        getSuccess(getURI() + format("?%s=1&%s=1", PARAM_SIZE, PARAM_PAGE)) //default ordering is "modified DESC"
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.pages", is(2)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.size", is(1)))
                .andExpect(jsonPath("$.next").doesNotExist())
                .andExpect(jsonPath("$.prev").exists())
                .andExpect(jsonPath("$.items[0].username", is(u1.getUsername())))
        ;

        getSuccess(getURI() + format("?%s=100", PARAM_SIZE))
                .andExpect(jsonPath("$.pages", is(1)))
                .andExpect(jsonPath("$.page", is(DEFAULT_PAGE)))
        ;
    }

}

