package com.noubase.idema.domain;

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
@SuppressWarnings("unused")
abstract class Model implements Persistable<String> {

    @Id
    private String id;

    @Indexed
    @CreatedDate
    private Date created;

    @Indexed
    @LastModifiedDate
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
