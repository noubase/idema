package com.noubase.idema.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noubase.idema.serialization.Internal;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Created by rshuper on 05.08.15.
 */
public class JSONTest extends ControllerTest {

    @Autowired
    private ObjectMapper restMapper;

    @Autowired
    private MappingJackson2HttpMessageConverter converter;

    @SuppressWarnings("unused")
    public static class Item {

        @JsonView(Internal.class)
        private String hidden;

        private String visible;

        public Item() {
        }

        public Item(String hidden, String visible) {
            this.hidden = hidden;
            this.visible = visible;
        }

        public String getHidden() {
            return hidden;
        }

        public void setHidden(String hidden) {
            this.hidden = hidden;
        }

        public String getVisible() {
            return visible;
        }

        public void setVisible(String visible) {
            this.visible = visible;
        }
    }

    public static class BoolItem {

        private boolean enabled = true;

        public BoolItem() {
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    private String body(Object object) throws Exception {
        MockHttpOutputMessage message = new MockHttpOutputMessage();
        converter.write(object, MediaType.APPLICATION_JSON, message);
        return message.getBody().toString();
    }

    @Test
    public void test() throws Exception {
        Item item = new Item("hidden", "visible");

        assertTrue(restMapper.writeValueAsString(item).contains("visible"));
        assertFalse(restMapper.writeValueAsString(item).contains("hidden"));

        String body = body(item);
        assertTrue(body.contains("visible"));
        assertFalse(body.contains("hidden"));

        item.setVisible(null);
        body = body(item);
        assertFalse(body.contains("visible"));
        assertThat(body.length(), is(2));
    }

    @Test
    public void testBooleans() throws Exception {

        BoolItem item = new BoolItem();
        String body = body(item);
        assertTrue(body.contains("enabled"));
        assertTrue(body.contains("true"));

        item.setEnabled(false);
        body = body(item);
        assertTrue(body.contains("false"));
    }
}
