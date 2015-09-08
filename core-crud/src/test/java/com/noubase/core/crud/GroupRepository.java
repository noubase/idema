package com.noubase.core.crud;

import com.noubase.core.crud.repository.ResourceRepository;
import org.bson.types.ObjectId;

/**
 * Created by rshuper on 18.08.15.
 */
@SuppressWarnings("unused")
public interface GroupRepository extends ResourceRepository<Group, ObjectId> {

}
