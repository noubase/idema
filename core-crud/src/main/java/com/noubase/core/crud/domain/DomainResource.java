package com.noubase.core.crud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noubase.core.crud.annotation.Unchangeable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.TextScore;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rshuper on 23.07.15.
 */
@SuppressWarnings("unused")
public abstract class DomainResource<T extends Serializable> implements Persistable<T> {

    @Id
    private T id;

    @TextScore
    Float score;

    Object $meta; //todo: remove after stable release of SDM?

    @Indexed
    @CreatedDate
    private Date created;

    @Indexed
    @LastModifiedDate
    private Date modified;

    public DomainResource() {
    }

    @Override
    @Unchangeable
    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    @Unchangeable
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Unchangeable
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
