package com.noubase.idema.controller;

import com.noubase.common.AbstractTest;
import com.noubase.idema.Application;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static com.noubase.util.TestUtil.convertObjectToJsonBytes;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    protected MockHttpServletRequestBuilder postJSON(String URI, Object body) throws IOException {
        return post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(body));
    }

    protected MockHttpServletRequestBuilder putJSON(String URI, Object body) throws IOException {
        return put(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(body));
    }

    protected ResultActions getJSON(String URI) throws Exception {
        return perform(get(URI));
    }

    protected ResultActions createSuccess(String URI, Object document) throws Exception {
        return create(URI, document)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected ResultActions getSuccess(String URI) throws Exception {
        return getJSON(URI)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected ResultActions updateSuccess(String URI, Object update) throws Exception {
        return update(URI, update)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected ResultActions create(String URI, Object document) throws Exception {
        return perform(postJSON(URI, document));
    }


    protected ResultActions update(String URI, Object document) throws Exception {
        return perform(putJSON(URI, document));
    }

    protected String getURI(Class tClass) {
        String key = tClass.getCanonicalName().toLowerCase();
        if (!routes.containsKey(key)) {
            RequestMapping requestMapping = (RequestMapping) tClass.getAnnotation(RequestMapping.class);
            routes.put(key, requestMapping.value()[0]);
        }

        return routes.get(key);
    }
}
