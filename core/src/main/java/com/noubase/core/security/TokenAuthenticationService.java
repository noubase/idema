package com.noubase.core.security;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

public class TokenAuthenticationService<U extends ExpirableUserDetails> {

    public static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";
    public static final long TEN_DAYS = 1000 * 60 * 60 * 24 * 10;

    @NotNull
    private final TokenHandler<U> tokenHandler;

    @Autowired
    public TokenAuthenticationService(Class<U> userClass, @Value("#token.secret ?: topSecret") String secret) {
        tokenHandler = new TokenHandler<>(DatatypeConverter.parseBase64Binary(secret), userClass);
    }

    public void addAuthentication(@NotNull HttpServletResponse response, @NotNull UserAuthentication<U> authentication) {
        final U user = authentication.getDetails();
        user.setExpires(System.currentTimeMillis() + TEN_DAYS);
        response.addHeader(AUTH_HEADER_NAME, tokenHandler.createTokenForUser(user));
    }

    @Nullable
    public Authentication getAuthentication(@NotNull HttpServletRequest request) {
        final String token = request.getHeader(AUTH_HEADER_NAME);
        if (token != null) {
            final ExpirableUserDetails user = tokenHandler.parseUserFromToken(token);
            if (user != null) {
                return new UserAuthentication<>(user);
            }
        }
        return null;
    }
}
