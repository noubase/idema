package com.noubase.core.security;

import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TokenUserDetailsService implements UserDetailsService {

    private SecurityUserRepository userRepo;

    public TokenUserDetailsService(SecurityUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public final ExpirableUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final ExpirableUserDetails byUsername = userRepo.findByUsername(username);
        if (byUsername == null) {
            throw new UsernameNotFoundException("user not found");
        }
        detailsChecker.check(byUsername);
        return byUsername;
    }
}
