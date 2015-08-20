package com.noubase.core.test.common;

import com.google.common.collect.Sets;
import com.noubase.core.domain.UserAuthority;
import com.noubase.core.security.ExpirableUserDetails;
import com.noubase.core.security.TokenAuthenticationService;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Created by rshuper on 05.08.15.
 */
public abstract class AbstractSecuredTest<U extends ExpirableUserDetails> extends AbstractTest {

    @Autowired
    private TokenAuthenticationService<U> authService;

    private final Class<U> userClass;

    protected AbstractSecuredTest(Class<U> userClass) {
        this.userClass = userClass;
    }

    private String token;

    protected void authenticatedAs(String username, String role) throws Exception {
        U u = userClass.newInstance();
        u.setUsername(username);
        u.setAuthorities(Sets.newHashSet(new UserAuthority(role)));
        u.setExpires(DateTime.now().getMillis() + TokenAuthenticationService.TEN_DAYS);
        token = authService.getTokenHandler().createTokenForUser(u);
    }

    @NotNull
    protected ResultActions perform(@NotNull MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder.header(TokenAuthenticationService.AUTH_HEADER_NAME, this.token));
    }
}
