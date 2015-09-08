package com.noubase.core.crud;

import com.noubase.core.crud.domain.DomainResource;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by rshuper on 04.09.15.
 */
@Document(collection = "core_crud_test_groups")
public class Group extends DomainResource<ObjectId> {

    @NotEmpty
    private String slug;

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
}
