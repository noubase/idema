package com.noubase.core.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rshuper on 24.10.14.
 * © egocube.com
 */
public final class TypeUtil {

    public static Boolean convertToBoolean(@NotNull String string) {
        return "1".equalsIgnoreCase(string.trim()) || "true".equalsIgnoreCase(string.trim());
    }

    private static final ConcurrentHashMap<String, Set<String>> boolFields = new ConcurrentHashMap<>();

    @NotNull
    public static Set<String> booleanFields(Class aClass) {
        if (!boolFields.containsKey(aClass.getCanonicalName())) {
            Set<String> set = new HashSet<>();
            for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(aClass)) {
                boolean primitive = pd.getPropertyType().isPrimitive()
                        && pd.getPropertyType().getCanonicalName().contains("boolean");
                if (primitive || pd.getPropertyType().isAssignableFrom(Boolean.class)) {
                    set.add(pd.getName());
                }
            }
            boolFields.put(aClass.getCanonicalName(), set);
        }
        return boolFields.get(aClass.getCanonicalName());
    }
}
