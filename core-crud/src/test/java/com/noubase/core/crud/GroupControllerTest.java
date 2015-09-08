package com.noubase.core.crud;

import com.github.fge.jsonpatch.JsonPatchOperation;
import com.noubase.core.crud.test.JsonPatchBuilder;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.noubase.core.crud.test.TestUtil.convertTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Created by rshuper on 08.09.15.
 */
public class GroupControllerTest extends AbstractIntegrationTest<User, ObjectId, Group> {

    @Autowired
    private GroupRepository repository;

    public GroupControllerTest() {
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

        List<JsonPatchOperation> operations = JsonPatchBuilder.create()
                .replace("/slug", "replaced").getOperations();

        String location = getLocation(patchSuccess(getURI(main), operations));
        getSuccess(location)
                .andExpect(jsonPath("$.slug", is(main.getSlug())))
        ;

        Group update = new Group("updated");
        Group updated = convertTo(updateSuccess(getURI(another), update), Group.class);
        assertEquals(update.getSlug(), updated.getSlug());
    }
}