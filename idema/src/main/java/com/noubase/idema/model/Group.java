package com.noubase.idema.model;

import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Set;

/**
 * Created by rshuper on 23.07.15.
 */
public class Group extends Model {

    @Indexed(unique = true)
    private String name;

    private Set<String> roles;

    private Boolean enabled;

    public Group() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
