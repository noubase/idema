package com.noubase.core.crud.config.security;

import com.noubase.core.security.ExpirableUserDetails;
import com.noubase.core.security.SecurityUserRepository;
import com.noubase.core.security.TokenAuthenticationService;
import com.noubase.core.security.TokenUserDetailsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
public abstract class StatelessAuthenticationSecurityConfig<U extends ExpirableUserDetails> extends WebSecurityConfigurerAdapter {

    @Value("#token.secret ?: default")
    protected String secret;

    private final Class<U> uClass;

    protected abstract SecurityUserRepository userRepo();

    protected StatelessAuthenticationSecurityConfig(Class<U> userClass) {
        super(true);
        this.uClass = userClass;
    }

    @Override
    protected void configure(@NotNull HttpSecurity http) throws Exception {

        StatelessLoginFilter<U> loginFilter = new StatelessLoginFilter<>("/api/login", uClass, authenticationService(), userDetailsService(), authenticationManager());

        http
                .csrf().disable()
                .exceptionHandling().and()
                .anonymous().and()
                .servletApi().and()
                .headers().cacheControl().and()
                .authorizeRequests()

                .antMatchers("/").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/resources/**").permitAll()

                .antMatchers(HttpMethod.POST, "/auth/login").permitAll()

                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                .antMatchers("/admin/**").hasRole("SUPER_ADMIN")

                //all other request need to be authenticated
                .anyRequest().hasRole("USER").and()

                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(new StatelessAuthenticationFilter(authenticationService()), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(@NotNull AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    @Override
    protected TokenUserDetailsService userDetailsService() {
        return new TokenUserDetailsService(userRepo());
    }

    @Bean
    protected TokenAuthenticationService<U> authenticationService() {
        return new TokenAuthenticationService<>(uClass, secret);
    }
}

