package com.noubase.idema.security;

import com.noubase.idema.domain.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

@Service
public class TokenAuthenticationService {

    public static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";
    public static final long TEN_DAYS = 1000 * 60 * 60 * 24 * 10;

    @NotNull
    private final TokenHandler tokenHandler;

    @Autowired
    public TokenAuthenticationService(@Value("${token.secret}") String secret) {
        tokenHandler = new TokenHandler(DatatypeConverter.parseBase64Binary(secret));
    }

    public void addAuthentication(@NotNull HttpServletResponse response, @NotNull UserAuthentication authentication) {
        final User user = authentication.getDetails();
        user.setExpires(System.currentTimeMillis() + TEN_DAYS);
        response.addHeader(AUTH_HEADER_NAME, tokenHandler.createTokenForUser(user));
    }

    @Nullable
    public Authentication getAuthentication(@NotNull HttpServletRequest request) {
        final String token = request.getHeader(AUTH_HEADER_NAME);
        if (token != null) {
            final User user = tokenHandler.parseUserFromToken(token);
            if (user != null) {
                return new UserAuthentication(user);
            }
        }
        return null;
    }
}
