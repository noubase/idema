package com.noubase.idema.repository;

import com.noubase.idema.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

/**
 * Created by rshuper on 23.07.15.
 */
@Component
public interface UserRepository extends MongoRepository<User, String> {

}
