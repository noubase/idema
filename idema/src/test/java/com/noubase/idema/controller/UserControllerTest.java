package com.noubase.idema.controller;

import com.google.common.collect.Sets;
import com.jayway.jsonpath.JsonPath;
import com.noubase.idema.domain.User;
import com.noubase.idema.model.CollectionRequest;
import com.noubase.idema.model.ResourceRequest;
import com.noubase.idema.model.search.SearchType;
import com.noubase.idema.repository.extend.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import static com.noubase.idema.model.CollectionRequest.*;
import static com.noubase.idema.model.search.SearchRequest.DELIMITER;
import static com.noubase.idema.util.DomainUtil.extractId;
import static com.noubase.util.TestUtil.convertTo;
import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hibernate.validator.internal.util.Contracts.assertNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rshuper on 24.07.15.
 */
public class UserControllerTest extends ControllerTest {

    @Autowired
    private UserRepository repository;

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
    public void before() {
        repository.deleteAll();
        authenticatedAs("super_admin", "ROLE_SUPER_ADMIN");
    }

    @Test
    public void testWithBadRole() throws Exception {
        authenticatedAs("user", "USER");
        get(getURI()).andExpect(status().is4xxClientError());
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
                .andExpect(jsonPath("$.size", is(CollectionRequest.DEFAULT_SIZE)))
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

    @Test
    public void testListAllSorting() throws Exception {
        User u1 = user("a_allSorting");
        u1.setLastName("zzz");
        User u2 = user("z_allSorting");
        u2.setLastName("aaa");
        u2.setFirstName("whatever");
        createSuccess(getURI(), u1);
        createSuccess(getURI(), u2);

        getSuccess(getURI() + format("?%s=lastName,desc", PARAM_ORDER))
                .andExpect(jsonPath("$.items[0].lastName", is(u1.getLastName())))
                .andExpect(jsonPath("$.items[1].lastName", is(u2.getLastName())))
        ;

        getSuccess(getURI() + format("?%s=username,desc", PARAM_ORDER))
                .andExpect(jsonPath("$.items[0].username", is(u2.getUsername())))
                .andExpect(jsonPath("$.items[1].username", is(u1.getUsername())))
        ;

        getSuccess(getURI() + format("?%s=firstName,asc", PARAM_ORDER))
                .andExpect(jsonPath("$.items[0].firstName").doesNotExist()) //null first by default in MongoDB
                .andExpect(jsonPath("$.items[1].firstName", is(u2.getFirstName())))
        ;
    }

    @Test
    public void testListAllParticularFields() throws Exception {
        User u1 = user("all_fields1");
        u1.setLastName("Last");
        User u2 = user("all_fields2");
        u2.setLastName("Name");
        u2.setFirstName("First");
        createSuccess(getURI(), u1);
        createSuccess(getURI(), u2);

        getSuccess(getURI() + format("?%s=lastName&%s=lastName,asc", PARAM_FIELDS, PARAM_ORDER))
                .andExpect(jsonPath("$.items[0].lastName", is(u1.getLastName())))
                .andExpect(jsonPath("$.items[1].lastName", is(u2.getLastName())))
                .andExpect(jsonPath("$.items[0].username").doesNotExist())
                .andExpect(jsonPath("$.items[1].username").doesNotExist())
        ;
    }

    @Test
    public void testListAllSearch() throws Exception {
        User u1 = user("john");
        u1.setFirstName("John");
        u1.setLastName("Smith");

        User u2 = user("smith_wesson");
        u2.setFirstName("Smith");
        u2.setLastName("Wesson");

        User u3 = user("joshua_jones");
        u3.setFirstName("Joshua");
        u3.setLastName("Jones Sm");

        createSuccess(getURI(), u1);
        createSuccess(getURI(), u2);
        createSuccess(getURI(), u3);

        String q = SearchType.LIKE + DELIMITER + "*" + DELIMITER + "es";
        getSuccess(getURI() + format("?%s=%s&%s=lastName,asc", PARAM_SEARCH, q, PARAM_ORDER))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.items[0].username", is(u3.getUsername())))
                .andExpect(jsonPath("$.items[1].username", is(u2.getUsername())))
        ;

        q = SearchType.LIKE + DELIMITER + "username" + DELIMITER + "it";
        getSuccess(getURI() + format("?%s=%s&%s=lastName,asc", PARAM_SEARCH, q, PARAM_ORDER))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.items[0].username", is(u2.getUsername())))
        ;

        q = SearchType.PREFIX + DELIMITER + "lastName" + DELIMITER + "sm";
        getSuccess(getURI() + format("?%s=%s&%s=lastName,asc", PARAM_SEARCH, q, PARAM_ORDER))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.items[0].username", is(u1.getUsername())))
        ;

        q = SearchType.EXACT + DELIMITER + "lastName" + DELIMITER + u3.getLastName();
        getSuccess(getURI() + format("?%s=%s", PARAM_SEARCH, q))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.items[0].username", is(u3.getUsername())))
        ;
    }

    @Test
    public void testListAllSearchTextScore() throws Exception {
        User u1 = user("hello");
        u1.setFirstName("John");

        User u2 = user("john");
        u2.setFirstName("Hello");

        createSuccess(getURI(), u1);
        createSuccess(getURI(), u2);

        String q = SearchType.EXACT + DELIMITER + "*" + DELIMITER + "hello";
        ResultActions actions = getSuccess(getURI() + format("?%s=%s&%s=1", PARAM_SEARCH, q, PARAM_SIZE))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.prev").doesNotExist())
                .andExpect(jsonPath("$.next").exists())
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.items[0].username", is(u1.getUsername()))); // username has bigger weight than firstName

        String next = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.next").toString();
        assertNotEmpty(next, "Next URL is empty");

        getSuccess(next)
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.next").doesNotExist())
                .andExpect(jsonPath("$.prev").exists())
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.items[0].username", is(u2.getUsername())));

    }

    /////////////////////////////////
    ///////////// CREATE ////////////
    /////////////////////////////////

    @Test
    public void testCreate() throws Exception {
        User user = user("test_user");
        user.setEnabled(true);
        ResultActions actions = createSuccess(this.getURI(), user);
        ResultActions success = getSuccess(getLocation(actions));
        success.andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.enabled", is(true)))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.salt").doesNotExist())
        ;
        User converted = convertTo(success, User.class);
        User reloaded = repository.findOne(converted.getId());
        assertEquals(reloaded.getId(), getResourceId(actions));
        assertNotEquals(user.getPassword(), reloaded.getPassword());
        assertNotEmpty(reloaded.getPassword(), "User password is empty");
        assertNotEmpty(reloaded.getSalt(), "User password salt is empty");
    }

    @Test
    public void testCreateWithoutPassword() throws Exception {
        User user = new User("test_user", null, "123");
        create(getURI(), user).andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateDuplicated() throws Exception {
        User user = user("duplicated_user");
        createSuccess(this.getURI(), user);
        create(this.getURI(), user)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field", is("username")))
                .andExpect(jsonPath("$.resource", is("user")))
        ;
    }


    /////////////////////////////////
    /////////////// READ ////////////
    /////////////////////////////////


    @Test
    public void testGet() throws Exception {
        User user = user("test_get_user");
        User converted = createAndConvert(user);
        getSuccess(getURI(converted)).andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    @Test
    public void testGet404() throws Exception {
        get(getURI() + "/whatever")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resource", is("user")))
                .andExpect(jsonPath("$.id", is("whatever")))
        ;
    }

    @Test
    public void testGetParticularFields() throws Exception {
        User user = user("test_get_particular_fields");
        user.setLastName("Last Name");
        user.setFirstName("First Name");
        User converted = createAndConvert(user);
        getSuccess(getURI(converted) + format("?%s=firstName,id", ResourceRequest.PARAM_FIELDS))
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.id", is(converted.getId())))
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.lastName").doesNotExist())
        ;
        getSuccess(getURI(converted))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.id", is(converted.getId())))
        ;
    }

    /////////////////////////////////
    ///////////// UPDATE ////////////
    /////////////////////////////////

    @Test
    public void testUpdate404() throws Exception {
        User update = new User("first", null);
        update(getURI() + "/whatever", update).andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateSuccess() throws Exception {
        User user = user("test_update_user");
        User convert = createAndConvert(user);

        User update = user("test_updated");
        update.setFirstName("Hello");
        update.setLastName("World");

        User updated = convertTo(updateSuccess(this.getURI(convert), update), User.class);
        assertEquals(update.getFirstName(), updated.getFirstName());
        assertEquals(update.getLastName(), updated.getLastName());
        assertEquals(update.getUsername(), updated.getUsername());
        assertNotEquals(convert.getModified(), updated.getModified());
    }

    @Test
    public void testUpdateDuplicated() throws Exception {
        User first = user("first");
        User second = user("second");
        createSuccess(getURI(), first);
        User convert = createAndConvert(second);

        User update = new User("first", null);
        update(getURI(convert), update).andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateWithoutRequired() throws Exception {
        User first = user("withoutRequired");
        User convert = createAndConvert(first);

        User update = new User("", null);
        update(getURI(convert), update).andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUnchangeable() throws Exception {
        User first = user("first");
        User convert = createAndConvert(first);
        User update = user("update");
        update.setId("123");
        update.setSalt("unchangeable");

        updateSuccess(getURI(convert), update);
        User reloaded = repository.findOne(convert.getId());

        assertEquals(update.getUsername(), reloaded.getUsername());
        assertNotEquals(update.getPassword(), reloaded.getPassword());
        assertNotEquals(update.getId(), reloaded.getId());
        assertNotEquals(update.getSalt(), reloaded.getSalt());
    }

    /////////////////////////////////
    ///////////// DELETE ////////////
    /////////////////////////////////

    @Test
    public void testDelete404() throws Exception {
        delete(getURI() + "/whatever").andExpect(status().isNotFound());
    }

    @Test
    public void testDelete() throws Exception {
        User forDelete = user("test_delete");
        String location = getLocation(createSuccess(getURI(), forDelete));
        deleteSuccess(location);
    }


    /////////////////////////////////
    //////// BATCH  DELETE //////////
    /////////////////////////////////

    @Test
    public void testDeleteBatchSuccess() throws Exception {
        User first = user("batch_delete_first");
        User convert = createAndConvert(first);

        User second = user("batch_delete_second");
        User convert2 = createAndConvert(second);

        deleteSuccess(getURI(), extractId(Sets.newHashSet(convert, convert2)));

        get(getURI(convert)).andExpect(status().isNotFound());
        get(getURI(convert2)).andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteBatch404() throws Exception {
        User first = user("batch_delete404_first");
        User convert = createAndConvert(first);

        delete(getURI(), Sets.newHashSet(convert.getId(), "whatever"))
                .andExpect(jsonPath("$.id").isArray())
                .andExpect(jsonPath("$.id", hasSize(1)))
                .andExpect(jsonPath("$.id[0]", is("whatever")))
                .andExpect(status().isNotFound())
        ;

        getSuccess(getURI(convert));
    }
}