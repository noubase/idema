package com.noubase.idema.controller;

import com.noubase.idema.domain.User;
import com.noubase.idema.model.CollectionRequest;
import com.noubase.idema.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import static com.noubase.idema.model.CollectionRequest.*;
import static com.noubase.util.TestUtil.convertTo;
import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hibernate.validator.internal.util.Contracts.assertNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    private String getURI(User user) {
        return format("%s/%s", getURI(), user.getId());
    }

    private User createAndConvert(User user) throws Exception {
        return convertTo(createSuccess(this.getURI(), user), User.class);
    }

    @Before
    public void before() {
        repository.deleteAll();
    }

    @Test
    public void testListAllPaging() throws Exception {
        User u1 = new User("a_listAll", "12345678");
        User u2 = new User("z_listAll", "12345678");
        createSuccess(getURI(), u1);
        createSuccess(getURI(), u2);
        getSuccess(getURI())
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.page", is(0)))
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
    }

    @Test
    public void testListAllSorting() throws Exception {
        User u1 = new User("a_allSorting", "12345678");
        u1.setLastName("zzz");
        User u2 = new User("z_allSorting", "12345678");
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
                .andDo(print())
                .andExpect(jsonPath("$.items[0].firstName").doesNotExist()) //null first by default in MongoDB
                .andExpect(jsonPath("$.items[1].firstName", is(u2.getFirstName())))
        ;
    }

    @Test
    public void testCreate() throws Exception {
        User user = new User("test_user", "12345678");
        ResultActions actions = createSuccess(this.getURI(), user);
        actions.andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.salt").doesNotExist())
        ;
        User converted = convertTo(actions, User.class);
        User reloaded = repository.findOne(converted.getId());
        assertNotEquals(user.getPassword(), reloaded.getPassword());
        assertNotEmpty(reloaded.getPassword(), "User password is empty");
        assertNotEmpty(reloaded.getSalt(), "User password salt is empty");
    }

    @Test
    public void testCreateWithoutPassword() throws Exception {
        User user = new User("test_user", null);
        create(getURI(), user).andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateDuplicated() throws Exception {
        User user = new User("duplicated_user", "12345678");
        createSuccess(this.getURI(), user);
        create(this.getURI(), user)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field", is("username")))
                .andExpect(jsonPath("$.resource", is("user")))
        ;
    }

    @Test
    public void testGet() throws Exception {
        User user = new User("test_get_user", "12345678");
        User converted = createAndConvert(user);
        getSuccess(getURI(converted)).andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    @Test
    public void testUpdateSuccess() throws Exception {
        User user = new User("test_update_user", "12345678");
        User convert = createAndConvert(user);

        User update = new User("test_updated", "12312312");
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
        User first = new User("first", "12345678");
        User second = new User("second", "12345678");
        createSuccess(getURI(), first);
        User convert = createAndConvert(second);

        User update = new User("first", null);
        update(getURI(convert), update).andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUnchangeable() throws Exception {
        User first = new User("first", "12345678");
        User convert = createAndConvert(first);
        User update = new User("update", "updated");
        update.setId("123");
        update.setSalt("unchangeable");

        updateSuccess(getURI(convert), update);
        User reloaded = repository.findOne(convert.getId());

        assertEquals(update.getUsername(), reloaded.getUsername());
        assertNotEquals(update.getPassword(), reloaded.getPassword());
        assertNotEquals(update.getId(), reloaded.getId());
        assertNotEquals(update.getSalt(), reloaded.getSalt());
    }

    @Test
    public void testDelete() throws Exception {
    }
}