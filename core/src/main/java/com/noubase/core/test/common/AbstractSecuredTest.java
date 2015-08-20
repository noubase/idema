package com.noubase.core.test.common;

import com.google.common.collect.Sets;
import com.noubase.core.domain.UserAuthority;
import com.noubase.core.security.ExpirableUserDetails;
import com.noubase.core.security.TokenAuthenticationService;
import com.noubase.core.security.TokenHandler;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.xml.bind.DatatypeConverter;

/**
 * Created by rshuper on 05.08.15.
 */
public abstract class AbstractSecuredTest<U extends ExpirableUserDetails> extends AbstractTest {

    private TokenHandler<U> tokenHandler;

    private final Class<U> userClass;

    protected AbstractSecuredTest(Class<U> userClass) {
        this.userClass = userClass;
    }

    @Autowired
    public void setTokenSecret(@Value("#token.secret ?: topSecret") String secret) {
        tokenHandler = new TokenHandler<>(DatatypeConverter.parseBase64Binary(secret), this.userClass);
    }

    private String token;

    protected void authenticatedAs(String username, String role) throws Exception {
        U u = userClass.newInstance();
        u.setUsername(username);
        u.setAuthorities(Sets.newHashSet(new UserAuthority(role)));
        u.setExpires(DateTime.now().getMillis() + TokenAuthenticationService.TEN_DAYS);
        token = tokenHandler.createTokenForUser(u);
    }

    @NotNull
    protected ResultActions perform(@NotNull MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder.header(TokenAuthenticationService.AUTH_HEADER_NAME, this.token));
    }
}
