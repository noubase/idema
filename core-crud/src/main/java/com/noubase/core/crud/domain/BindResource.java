package com.noubase.core.crud.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rshuper on 07.09.15.
 */
public class BindResource<T extends Serializable> {
    @Id
    private T id;

    @Indexed
    @CreatedDate
    private Date created;

    public BindResource() {
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
