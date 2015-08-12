package com.noubase.idema.controller;

import com.noubase.common.AbstractTest;
import com.noubase.idema.Application;
import com.noubase.idema.model.Headers;
import com.noubase.util.TestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static com.noubase.util.TestUtil.convertObjectToJsonBytes;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rshuper on 24.07.15.
 */
@WebIntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@Configuration
@ActiveProfiles(profiles = "test")
abstract class ControllerTest extends AbstractTest {


    private final ConcurrentHashMap<String, String> routes = new ConcurrentHashMap<>();

    protected MockHttpServletRequestBuilder postJSON(@NotNull String URI, Object body) throws IOException {
        return post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(body));
    }

    protected MockHttpServletRequestBuilder putJSON(@NotNull String URI, Object body) throws IOException {
        return put(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(body));
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
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected ResultActions getSuccess(@NotNull String URI) throws Exception {
        return get(URI)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected ResultActions updateSuccess(@NotNull String URI, Object update) throws Exception {
        return update(URI, update)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected ResultActions deleteSuccess(@NotNull String URI, Object document) throws Exception {
        return delete(URI, document)
                .andExpect(status().isNoContent());
    }

    protected ResultActions deleteSuccess(@NotNull String URI) throws Exception {
        return delete(URI, null)
                .andExpect(status().isNoContent());
    }

    protected ResultActions get(@NotNull String URI) throws Exception {
        return perform(getJSON(URI));
    }

    protected ResultActions create(@NotNull String URI, Object document) throws Exception {
        return perform(postJSON(URI, document));
    }

    protected ResultActions update(@NotNull String URI, Object document) throws Exception {
        return perform(putJSON(URI, document));
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
