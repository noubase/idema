package com.noubase.idema.repository;

import com.noubase.core.crud.repository.CRUDRepository;
import com.noubase.core.security.SecurityUserRepository;
import com.noubase.idema.domain.User;
import org.jetbrains.annotations.Nullable;

/**
 * Created by rshuper on 23.07.15.
 */
@SuppressWarnings("unused")
public interface UserRepository extends CRUDRepository<User, String>, SecurityUserRepository<User> {

    @Nullable
    User findByUsername(String username);
}
