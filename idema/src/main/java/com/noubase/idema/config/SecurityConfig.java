package com.noubase.idema.config;

import com.noubase.core.crud.config.security.StatelessAuthenticationSecurityConfig;
import com.noubase.core.security.SecurityUserRepository;
import com.noubase.idema.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rshuper on 20.08.15.
 */
@Configuration
public class SecurityConfig extends StatelessAuthenticationSecurityConfig<User> {

    @Autowired
    private SecurityUserRepository<User> userRepository;

    protected SecurityConfig() {
        super(User.class);
    }

    @Override
    protected SecurityUserRepository userRepo() {
        return userRepository;
    }
}
