package com.noubase.core.crud;

import com.noubase.core.crud.domain.BindResource;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by rshuper on 07.09.15.
 */
@Document(collection = "core_crud_test_group_user")
@CompoundIndexes({
        @CompoundIndex(name = "pair", unique = true, def = "{'uid': 1, 'gid': 1}")}
)
public class GroupUser extends BindResource<String> {

    @Indexed
    private String uid;

    @Indexed
    private String gid;

    public GroupUser() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }
}
