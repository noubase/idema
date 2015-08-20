package com.noubase.core.test.common;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

/**
 * Created by rshuper on 17.08.15.
 */
@WebIntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@Configuration
@ActiveProfiles(profiles = "test")
public abstract class AbstractTest {

    protected static MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Autowired
    private Filter springSecurityFilterChain;

    @Before
    public void setUp() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                    .addFilters(springSecurityFilterChain)
                    .build();
        }
    }
}
