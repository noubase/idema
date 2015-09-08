package com.noubase.core.crud.test;

import com.github.fge.jsonpatch.JsonPatchOperation;
import com.noubase.core.crud.model.Headers;
import com.noubase.core.security.ExpirableUserDetails;
import com.noubase.core.test.common.AbstractSecuredTest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Created by rshuper on 24.07.15.
 */
public abstract class AbstractControllerTest<U extends ExpirableUserDetails> extends AbstractSecuredTest<U> {

    private final ConcurrentHashMap<String, String> routes = new ConcurrentHashMap<>();

    protected AbstractControllerTest(Class<U> userClass) {
        super(userClass);
    }

    protected MockHttpServletRequestBuilder postJSON(@NotNull String URI, Object body) throws IOException {
        return MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(body));
    }

    protected MockHttpServletRequestBuilder patchJSON(@NotNull String URI, List<JsonPatchOperation> operations) throws IOException {
        return MockMvcRequestBuilders.patch(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(operations));
    }

    protected MockHttpServletRequestBuilder putJSON(@NotNull String URI, Object body) throws IOException {
        return MockMvcRequestBuilders.put(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(body));
    }

    @NotNull
    protected MockHttpServletRequestBuilder getJSON(@NotNull String URI) {
        return MockMvcRequestBuilders.get(URI);
    }

    protected MockHttpServletRequestBuilder deleteJson(@NotNull String URI, @Nullable Object body) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(URI)
                .contentType(MediaType.APPLICATION_JSON);
        if (body != null) {
            builder.content(TestUtil.convertObjectToJsonBytes(body));
        }
        return builder;
    }

    protected ResultActions createSuccess(@NotNull String URI, Object document) throws Exception {
        return create(URI, document)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected ResultActions getSuccess(@NotNull String URI) throws Exception {
        return get(URI)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected ResultActions updateSuccess(@NotNull String URI, Object update) throws Exception {
        return update(URI, update)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected ResultActions patchSuccess(@NotNull String URI, List<JsonPatchOperation> operations) throws Exception {
        return patch(URI, operations)
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected ResultActions deleteSuccess(@NotNull String URI, Object document) throws Exception {
        return delete(URI, document)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    protected ResultActions deleteSuccess(@NotNull String URI) throws Exception {
        return delete(URI, null)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    protected ResultActions get(@NotNull String URI) throws Exception {
        return perform(getJSON(URI));
    }

    protected ResultActions create(@NotNull String URI, @NotNull Object document) throws Exception {
        return perform(postJSON(URI, document));
    }

    protected ResultActions update(@NotNull String URI, Object document) throws Exception {
        return perform(putJSON(URI, document));
    }

    protected ResultActions patch(@NotNull String URI, List<JsonPatchOperation> operations) throws Exception {
        return perform(patchJSON(URI, operations));
    }

    protected ResultActions delete(@NotNull String URI, Object document) throws Exception {
        return perform(deleteJson(URI, document));
    }

    protected ResultActions delete(@NotNull String URI) throws Exception {
        return perform(deleteJson(URI, null));
    }

    protected String getLocation(@NotNull ResultActions actions) {
        return actions.andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
    }

    protected String getResourceId(@NotNull ResultActions actions) {
        return actions.andReturn().getResponse().getHeader(Headers.RESOURCE_ID);
    }

    protected String getURI(@NotNull Class tClass) {
        String key = tClass.getCanonicalName().toLowerCase();
        if (!routes.containsKey(key)) {
            RequestMapping requestMapping = (RequestMapping) tClass.getAnnotation(RequestMapping.class);
            routes.put(key, requestMapping.value()[0]);
        }

        return routes.get(key);
    }
}
