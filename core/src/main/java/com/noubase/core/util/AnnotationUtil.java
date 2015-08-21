package com.noubase.core.util;

import org.jetbrains.annotations.NotNull;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
            Set<String> fields = new HashSet<>();
            for (Field field : aClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ann)) {
                    fields.add(field.getName());
                }
            }
            PropertyDescriptor[] descriptors = getPropertyDescriptors(aClass);
            for (PropertyDescriptor pd : descriptors) {
                String name = pd.getName();
                if (fields.contains(name)) {
                    continue;
                }
                if ((pd.getReadMethod() != null && pd.getReadMethod().isAnnotationPresent(ann))
                        || (pd.getWriteMethod() != null && pd.getWriteMethod().isAnnotationPresent(ann))) {
                    fields.add(name);
                }
            }
            annotatedFields.put(key, fields);
        }
        return annotatedFields.get(key);
    }
}
