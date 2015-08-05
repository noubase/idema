package com.noubase.idema.domain;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

public class UserAuthority implements GrantedAuthority {

    @NotEmpty
    private String authority;

    public UserAuthority() {
    }

    public UserAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserAuthority))
            return false;

        UserAuthority ua = (UserAuthority) obj;
        return Objects.equals(ua.getAuthority(), this.getAuthority()) || ua.getAuthority().equals(this.getAuthority());
    }

    @Override
    public int hashCode() {
        return getAuthority() == null ? 0 : getAuthority().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getAuthority();
    }
}
