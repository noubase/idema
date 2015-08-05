package com.noubase.idema.repository;

import com.noubase.idema.domain.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by rshuper on 23.07.15.
 */
public interface GroupRepository extends MongoRepository<Group, String> {

}
