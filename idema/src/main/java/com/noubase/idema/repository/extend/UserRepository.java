package com.noubase.idema.repository.extend;

import com.noubase.idema.domain.User;
import com.noubase.idema.repository.CRUDRepository;
import org.jetbrains.annotations.Nullable;

/**
 * Created by rshuper on 23.07.15.
 */
public interface UserRepository extends CRUDRepository<User, String> {

    @Nullable
    User findByUsername(String username);
}
