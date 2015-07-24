package com.noubase.idema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noubase.idema.annotation.Unchangeable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

/**
 * Created by rshuper on 23.07.15.
 */
abstract class Model implements Persistable<String> {

    @Id
    private String id;

    @CreatedDate
    @Indexed
    private Date created;

    @LastModifiedDate
    @Indexed
    private Date modified;

    public Model() {
    }

    @Override
    @Unchangeable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return getId() == null;
    }
}
