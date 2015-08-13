package com.noubase.idema.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rshuper on 13.08.15.
 */
public class DomainUtil {

    @NotNull
    public static <ID extends Serializable> Set<ID> extractId(@NotNull Iterable<? extends Persistable<ID>> list) {
        Set<ID> set = new HashSet<>();
        for (Persistable<ID> o : list) {
            set.add(o.getId());
        }
        return set;
    }
}
