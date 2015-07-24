package com.noubase.idema.controller;

import com.noubase.idema.Application;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static com.noubase.util.TestUtil.convertObjectToJsonBytes;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rshuper on 24.07.15.
 */
@WebIntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@Configuration
abstract class ControllerTest {
    private final ConcurrentHashMap<String, String> routes = new ConcurrentHashMap<>();
    private static MockMvc mockMvc;
    private WebApplicationContext webApplicationContext;

    @Autowired
    public void setWebApplicationContext(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @Before
    public void setUp() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                    .build();
        }
    }

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
        return mockMvc.perform(get(URI));
    }

    protected ResultActions createSuccess(String URI, Object document) throws Exception {
        return create(URI, document)
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
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }


    protected ResultActions create(String URI, Object document) throws Exception {
        return mockMvc.perform(postJSON(URI, document));
    }

    protected ResultActions update(String URI, Object document) throws Exception {
        return mockMvc.perform(putJSON(URI, document));
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
