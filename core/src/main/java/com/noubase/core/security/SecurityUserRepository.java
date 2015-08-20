package com.noubase.core.security;

/**
 * Created by rshuper on 17.08.15.
 */
public interface SecurityUserRepository {

    ExpirableUserDetails findByUsername(String username);
}
