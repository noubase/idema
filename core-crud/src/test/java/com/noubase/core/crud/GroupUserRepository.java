package com.noubase.core.crud;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by rshuper on 07.09.15.
 */
@SuppressWarnings("unused")
public interface GroupUserRepository extends MongoRepository<GroupUser, String> {
}
