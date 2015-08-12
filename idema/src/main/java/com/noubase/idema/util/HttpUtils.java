package com.noubase.idema.util;

import com.google.common.base.Joiner;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rshuper on 12.11.14.
 * © egocube.com
 */
public final class HttpUtils {

    @NotNull
    public static String buildQuery(String base, @NotNull Map<String, String> params) {
        String queryString = Joiner.on("&").withKeyValueSeparator("=").join(params);
        return base + "?" + queryString;
    }

    @NotNull
    public static String buildQueryFromMap(String base, @NotNull Map<String, String[]> params) {
        HashMap<String, String> converted = new HashMap<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            converted.put(entry.getKey(), entry.getValue()[0]);
        }
        return buildQuery(base, converted);
    }

    @NotNull
    public static Map<String, String[]> replaceParameter(@NotNull Map<String, String[]> params, String name, String value) {
        String[] newValue = new String[]{value};
        if (params.containsKey(name)) {
            params.replace(name, newValue);
        } else {
            params.put(name, newValue);
        }
        return params;
    }
}
