package com.noubase.core.crud;

import com.noubase.core.crud.repository.CRUDRepository;
import org.jetbrains.annotations.Nullable;

/**
 * Created by rshuper on 18.08.15.
 */
@SuppressWarnings("unused")
public interface UserRepository extends CRUDRepository<User, String> {

    @Nullable
    User findByUsername(String username);
}
