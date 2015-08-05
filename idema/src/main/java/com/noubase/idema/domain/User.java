package com.noubase.idema.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.noubase.idema.annotation.Unchangeable;
import com.noubase.idema.serialization.Internal;
import com.noubase.idema.validation.CreateResource;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Created by rshuper on 23.07.15.
 */
@Document(collection = "users")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User extends Model {

    private String firstName;

    private String lastName;

    @NotEmpty
    @Indexed(unique = true)
    @Size(min = 3, max = 100)
    private String username;

    @JsonView(Internal.class)
    @NotEmpty(groups = CreateResource.class)
    @Size(min = 8, max = 24, groups = CreateResource.class)
    private String password;

    @JsonView(Internal.class)
    private String salt;

    private Set<String> roles;

    @Indexed
    private Boolean enabled;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
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

    public void setUsername(String username) {
        this.username = username;
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

    @Override
    public String toString() {
        return "User{" +
                " firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", roles=" + roles +
                ", enabled=" + enabled +
                "} " + super.toString();
    }
}
