package com.noubase.idema.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Set;

/**
 * Created by rshuper on 23.07.15.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Group extends Model {

    @Indexed(unique = true)
    private String name;

    private Set<String> roles;

    @Indexed
    private boolean enabled = true;

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

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
