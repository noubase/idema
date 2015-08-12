package com.noubase.idema.util;

import org.jetbrains.annotations.NotNull;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.beans.BeanUtils.getPropertyDescriptors;

/**
 * Created by rshuper on 24.07.15.
 */
public final class AnnotationUtil {

    private static final ConcurrentHashMap<String, Set<String>> annotatedFields = new ConcurrentHashMap<>();

    public static Set<String> getFieldsByAnnotation(@NotNull Class aClass, @NotNull Class<? extends Annotation> ann) {
        String key = aClass.getCanonicalName() + ann.getCanonicalName();
        if (!annotatedFields.containsKey(key)) {
            PropertyDescriptor[] descriptors = getPropertyDescriptors(aClass);
            Set<String> fields = new HashSet<>();
            for (PropertyDescriptor pd : descriptors) {
                if (pd.getReadMethod() != null && pd.getReadMethod().isAnnotationPresent(ann)) {
                    fields.add(pd.getName());
                }
            }
            annotatedFields.put(key, fields);
        }
        return annotatedFields.get(key);
    }
}
