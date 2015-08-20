package com.noubase.core.crud.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noubase.core.security.ExpirableUserDetails;
import com.noubase.core.security.TokenAuthenticationService;
import com.noubase.core.security.TokenUserDetailsService;
import com.noubase.core.security.UserAuthentication;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class StatelessLoginFilter<U extends ExpirableUserDetails> extends AbstractAuthenticationProcessingFilter {

    private final TokenAuthenticationService<U> tokenAuthenticationService;
    private final TokenUserDetailsService userDetailsService;
    private final Class<U> uClass;

    protected StatelessLoginFilter(
            @NotNull String urlMapping,
            Class<U> uClass,
            TokenAuthenticationService<U> authService,
            TokenUserDetailsService userService,
            AuthenticationManager authManager
    ) {
        super(new AntPathRequestMatcher(urlMapping));
        this.uClass = uClass;
        this.userDetailsService = userService;
        this.tokenAuthenticationService = authService;
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(@NotNull HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        final U user = new ObjectMapper().readValue(request.getInputStream(), uClass);
        final UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword());
        return getAuthenticationManager().authenticate(loginToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, @NotNull Authentication authentication) throws IOException, ServletException {

        // Lookup the complete User object from the database and create an Authentication for it
        final ExpirableUserDetails authenticatedUser = userDetailsService.loadUserByUsername(authentication.getName());
        final UserAuthentication userAuthentication = new UserAuthentication(authenticatedUser);

        // Add the custom token as HTTP header to the response
        tokenAuthenticationService.addAuthentication(response, userAuthentication);

        // Add the authentication to the Security context
        SecurityContextHolder.getContext().setAuthentication(userAuthentication);
    }
}