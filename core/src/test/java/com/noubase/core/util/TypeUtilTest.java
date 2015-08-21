package com.noubase.core.util;

import org.junit.Test;

import java.util.Set;

import static com.noubase.core.util.TypeUtil.booleanFields;
import static com.noubase.core.util.TypeUtil.convertToBoolean;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by rshuper on 21.08.15.
 */
public class TypeUtilTest {

    @Test
    public void testConvertToBoolean() throws Exception {
        assertTrue(convertToBoolean("1"));
        assertTrue(convertToBoolean("true"));
        assertFalse(convertToBoolean("0"));
        assertFalse(convertToBoolean("false"));
        assertFalse(convertToBoolean("whatever"));
    }

    @SuppressWarnings("unused")
    static class BooleanFields {

        private boolean one;

        private Boolean two;

        private String three;

        public boolean isOne() {
            return one;
        }

        public Boolean getTwo() {
            return two;
        }

        public String getThree() {
            return three;
        }

        public void setOne(boolean one) {
            this.one = one;
        }

        public void setTwo(Boolean two) {
            this.two = two;
        }

        public void setThree(String three) {
            this.three = three;
        }
    }

    @Test
    public void testBooleanFields() throws Exception {
        Set<String> set = booleanFields(BooleanFields.class);
        assertTrue(set.contains("one"));
        assertTrue(set.contains("two"));
        assertFalse(set.contains("three"));
    }
}