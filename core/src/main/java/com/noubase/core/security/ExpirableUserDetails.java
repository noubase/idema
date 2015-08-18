package com.noubase.core.security;

import com.noubase.core.domain.UserAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created by rshuper on 17.08.15.
 */
public interface ExpirableUserDetails extends UserDetails {

    void setExpires(Long time);

    Long getExpires();

    void setUsername(String username);

    void setAuthorities(Set<UserAuthority> authorities);

}
