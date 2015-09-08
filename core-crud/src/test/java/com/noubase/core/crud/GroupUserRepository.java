package com.noubase.core.crud;

import com.noubase.core.crud.repository.ResourceBindingRepository;
import org.bson.types.ObjectId;

/**
 * Created by rshuper on 07.09.15.
 */
@SuppressWarnings("unused")
public interface GroupUserRepository extends ResourceBindingRepository<ObjectId, String, GroupUser> {
}
