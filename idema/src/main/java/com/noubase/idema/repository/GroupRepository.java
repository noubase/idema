package com.noubase.idema.repository;

import com.noubase.idema.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

/**
 * Created by rshuper on 23.07.15.
 */
@Component
public interface GroupRepository extends MongoRepository<Group, String> {

}
