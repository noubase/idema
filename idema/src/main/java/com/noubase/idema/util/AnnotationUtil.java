package com.noubase.idema.util;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.beans.BeanUtils.getPropertyDescriptors;

/**
 * Created by rshuper on 24.07.15.
 */
public class AnnotationUtil {

    public static Set<String> getFieldsByAnnotation(Class aClass, Class<? extends Annotation> ann) {
        PropertyDescriptor[] descriptors = getPropertyDescriptors(aClass);
        HashSet<String> ignore = new HashSet<>();
        for (PropertyDescriptor pd : descriptors) {
            if (pd.getReadMethod() != null && pd.getReadMethod().isAnnotationPresent(ann)) {
                ignore.add(pd.getName());
            }
        }
        return ignore;
    }

    public static <T> T callMethodsByAnnotation(T o, Class aClass, Class<? extends Annotation> ann, Object value) throws InvocationTargetException, IllegalAccessException {
        PropertyDescriptor[] descriptors = getPropertyDescriptors(aClass);
        for (PropertyDescriptor pd : descriptors) {
            if (pd.getReadMethod().isAnnotationPresent(ann)) {
                pd.getWriteMethod().invoke(o, value);
            }
        }
        return o;
    }
}
