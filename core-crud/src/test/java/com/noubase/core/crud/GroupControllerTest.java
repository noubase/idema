package com.noubase.core.crud;

import com.github.fge.jsonpatch.JsonPatchOperation;
import com.google.common.collect.Sets;
import com.noubase.core.crud.test.JsonPatchBuilder;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.noubase.core.crud.model.CollectionRequest.*;
import static com.noubase.core.crud.test.TestUtil.convertTo;
import static com.noubase.core.crud.util.DomainUtil.extractId;
import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        Group third = createAndConvert(new Group("third"));

        List<JsonPatchOperation> operations = JsonPatchBuilder.create()
                .replace("/slug", "replaced").getOperations();

        String location = getLocation(patchSuccess(getURI(main), operations));
        getSuccess(location).andExpect(jsonPath("$.slug", is("replaced")));

        Group update = new Group("updated");
        Group updated = convertTo(updateSuccess(getURI(another), update), Group.class);
        assertEquals(update.getSlug(), updated.getSlug());

        getSuccess(getURI() + format("?%s=2&%s=slug,desc", PARAM_SIZE, PARAM_ORDER))
                .andExpect(jsonPath("$.total", is(3)))
                .andExpect(jsonPath("$.page", is(DEFAULT_PAGE)))
                .andExpect(jsonPath("$.pages", is(2)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.size", is(2)))
                .andExpect(jsonPath("$.next").exists())
                .andExpect(jsonPath("$.prev").doesNotExist())
                .andExpect(jsonPath("$.items[0].slug", is(updated.getSlug())))
        ;

        deleteSuccess(getURI(main));
        deleteSuccess(getURI(), extractId(Sets.newHashSet(another, third)));

        getSuccess(getURI()).andExpect(jsonPath("$.total", is(0)));
    }

    @Test
    public void patchNotEmptyToEmpty() throws Exception {
        String location = getLocation(createSuccess(getURI(), new Group("not_empty")));

        List<JsonPatchOperation> operations = JsonPatchBuilder.create()
                .remove("/slug").getOperations();

        patch(location, operations)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("slug")))
                .andExpect(jsonPath("$.fieldErrors[0].resource", is(Group.class.getCanonicalName())))
        ;

        getSuccess(location).andExpect(jsonPath("$.slug", is("not_empty")));
    }

    @Test
    public void createdDateAfterUpdate() throws Exception {
        String location = getLocation(createSuccess(getURI(), new Group("created_after_update")));
        Group update = new Group("updated");
        updateSuccess(location, update);
        getSuccess(location)
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.modified").exists())
        ;
    }


}