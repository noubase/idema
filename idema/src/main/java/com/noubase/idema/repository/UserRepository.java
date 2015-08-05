package com.noubase.idema.repository;

import com.noubase.idema.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by rshuper on 23.07.15.
 */
public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);
}
