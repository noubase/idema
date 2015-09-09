package com.noubase.core.crud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noubase.core.crud.annotation.Unchangeable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by rshuper on 07.09.15.
 */
@CompoundIndex(name = "pair", unique = true, def = "{'p': 1, 's': 1}")
public abstract class BindResource<P extends Serializable, S extends Serializable> implements Persistable<String> {

    @Id
    private String id;

    @Indexed
    @CreatedDate
    private Date created;

    @Indexed
    @Field(value = "p")
    @NotNull
    private P primary;

    @Indexed
    @Field(value = "s")
    @NotNull
    private S secondary;

    public BindResource() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Unchangeable
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public P getPrimary() {
        return primary;
    }

    public void setPrimary(P primary) {
        this.primary = primary;
    }

    public S getSecondary() {
        return secondary;
    }

    public void setSecondary(S secondary) {
        this.secondary = secondary;
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return getId() == null;
    }
}
