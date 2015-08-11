package com.noubase.idema.repository;

import com.noubase.idema.domain.User;

/**
 * Created by rshuper on 23.07.15.
 */
public interface UserRepository extends CRUDRepository<User, String> {

    User findByUsername(String username);
}
