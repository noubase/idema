package com.noubase.core.crud;

import com.noubase.core.crud.annotation.Unchangeable;
import com.noubase.core.crud.domain.DomainResource;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;

/**
 * Created by rshuper on 04.09.15.
 */
@Document(collection = "core_crud_test_groups")
public class Group extends DomainResource<ObjectId> {

    @NotEmpty
    private String slug;

    @Transient
    private Collection<User> users;

    public Group() {
    }

    public Group(String slug) {
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Unchangeable
    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }
}
