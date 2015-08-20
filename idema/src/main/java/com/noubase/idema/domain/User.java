package com.noubase.idema.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.noubase.core.crud.annotation.Unchangeable;
import com.noubase.core.crud.domain.DomainResource;
import com.noubase.core.crud.serialization.Internal;
import com.noubase.core.crud.validation.CreateResource;
import com.noubase.core.domain.UserAuthority;
import com.noubase.core.security.ExpirableUserDetails;
import org.hibernate.validator.constraints.NotEmpty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rshuper on 23.07.15.
 */
@Document(collection = "users")
public class User extends DomainResource<String> implements ExpirableUserDetails {

    @TextIndexed(weight = 1.5F)
    private String firstName;

    @TextIndexed(weight = 1.1F)
    private String lastName;

    @Transient
    private Long expires;

    @NotEmpty
    @Indexed(unique = true)
    @Size(min = 3, max = 100)
    @TextIndexed(weight = 2)
    private String username;

    @JsonView(Internal.class)
    @NotEmpty(groups = CreateResource.class)
    @Size(min = 8, max = 24, groups = CreateResource.class)
    private String password;

    @JsonView(Internal.class)
    private String salt;

    @NotEmpty(groups = CreateResource.class)
    private Set<UserAuthority> authorities = new HashSet<>();

    @Indexed
    private boolean enabled = true;

    @SuppressWarnings("unused")
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, @NotNull String... authorities) {
        this.username = username;
        this.password = password;
        for (String a : authorities) {
            this.authorities.add(new UserAuthority(a));
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<UserAuthority> authorities) {
        this.authorities = authorities;
    }

    @Unchangeable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Unchangeable
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

    @NotNull
    @Override
    public String toString() {
        return "User{" +
                " firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", authorities=" + authorities +
                ", enabled=" + enabled +
                "} " + super.toString();
    }
}
