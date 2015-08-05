package com.noubase.common;

import com.google.common.collect.Sets;
import com.noubase.idema.domain.User;
import com.noubase.idema.domain.UserAuthority;
import com.noubase.idema.security.TokenAuthenticationService;
import com.noubase.idema.security.TokenHandler;
import org.joda.time.DateTime;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import javax.xml.bind.DatatypeConverter;

/**
 * Created by rshuper on 05.08.15.
 */
public abstract class AbstractTest {

    private static MockMvc mockMvc;

    private TokenHandler tokenHandler;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    public void setTokenSecret(@Value("${token.secret}") String secret) {
        tokenHandler = new TokenHandler(DatatypeConverter.parseBase64Binary(secret));
    }

    private String token;

    protected void authenticatedAs(String username, String role) {
        User u = new User(username, "");
        u.setAuthorities(Sets.newHashSet(new UserAuthority(role)));
        u.setExpires(DateTime.now().getMillis() + TokenAuthenticationService.TEN_DAYS);
        token = tokenHandler.createTokenForUser(u);
    }

    @Before
    public void setUp() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                    .addFilters(springSecurityFilterChain)
                    .build();
        }
    }

    protected ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder.header(TokenAuthenticationService.AUTH_HEADER_NAME, this.token));
    }
}
