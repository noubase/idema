package com.noubase.core.crud;

import com.noubase.core.crud.domain.BindResource;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by rshuper on 07.09.15.
 */
@Document(collection = "core_crud_test_group_user")
public class GroupUser extends BindResource<ObjectId, String> {

    @SuppressWarnings("unused")
    public GroupUser() {
    }
}
