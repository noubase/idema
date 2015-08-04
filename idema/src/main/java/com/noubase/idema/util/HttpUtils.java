package com.noubase.idema.util;

import com.google.common.base.Joiner;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rshuper on 12.11.14.
 * © egocube.com
 */
public class HttpUtils {

    public static String buildQuery(String base, @NotNull Map<String, String> params) {
        String queryString = Joiner.on("&").withKeyValueSeparator("=").join(params);
        return base + "?" + queryString;
    }

    public static String buildQueryFromMap(String base, @NotNull Map<String, String[]> params) {
        HashMap<String, String> converted = new HashMap<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            converted.put(entry.getKey(), entry.getValue()[0]);
        }
        return buildQuery(base, converted);
    }

    public static Map<String, String[]> replaceParameter(@NotNull Map<String, String[]> params, String name, String value) {
        Assert.notNull(params, "Parameters map cannot be null");
        String[] newValue = new String[]{value};
        if (params.containsKey(name)) {
            params.replace(name, newValue);
        } else {
            params.put(name, newValue);
        }
        return params;
    }
}
