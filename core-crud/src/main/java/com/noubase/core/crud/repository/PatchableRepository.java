package com.noubase.core.crud.repository;

import org.jetbrains.annotations.NotNull;

/**
 * Created by rshuper on 13.08.15.
 */
interface PatchableRepository {

    void setJsonPatcher(@NotNull JsonPatcher patcher);
}
