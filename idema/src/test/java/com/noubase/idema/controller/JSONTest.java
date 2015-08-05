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

    @Test
    public void test() throws Exception {
        Item item = new Item("hidden", "visible");

        assertTrue(restMapper.writeValueAsString(item).contains("visible"));
        assertFalse(restMapper.writeValueAsString(item).contains("hidden"));

        MockHttpOutputMessage message = new MockHttpOutputMessage();
        converter.write(item, MediaType.APPLICATION_JSON, message);
        String body = message.getBody().toString();
        assertTrue(body.contains("visible"));
        assertFalse(body.contains("hidden"));

        item.setVisible(null);
        message = new MockHttpOutputMessage();
        converter.write(item, MediaType.APPLICATION_JSON, message);
        body = message.getBody().toString();
        assertFalse(body.contains("visible"));
        assertThat(body.length(), is(2));
    }
}
