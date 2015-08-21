package com.noubase.core.util;

import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

import static com.noubase.core.util.AnnotationUtil.getFieldsByAnnotation;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by rshuper on 21.08.15.
 */
public class AnnotationUtilTest {

    @SuppressWarnings("unused")
    static class Annotated {

        @TestAnnotation
        private String one;

        private String two;

        @TestAnnotation
        public String getTwo() {
            return two;
        }

        private String three;

        public String getThree() {
            return three;
        }

        public void setThree(String three) {
            this.three = three;
        }
    }

    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation {

    }


    @Test
    public void testGetFieldsByAnnotation() throws Exception {
        Set<String> set = getFieldsByAnnotation(Annotated.class, TestAnnotation.class);
        assertTrue(set.contains("one"));
        assertTrue(set.contains("two"));
        assertFalse(set.contains("three"));
    }
}