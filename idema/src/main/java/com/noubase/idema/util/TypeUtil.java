package com.noubase.idema.util;

import org.jetbrains.annotations.NotNull;

/**
 * Created by rshuper on 24.10.14.
 * © egocube.com
 */
public final class TypeUtil {

    public static Boolean convertToBoolean(@NotNull String string) {
        return "1".equalsIgnoreCase(string.trim()) || "true".equalsIgnoreCase(string.trim());
    }
}
