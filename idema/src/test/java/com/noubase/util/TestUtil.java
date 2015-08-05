package com.noubase.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noubase.idema.serialization.RESTObjectMapper;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;

/**
 * Created by rshuper on 24.07.15.
 */
public final class TestUtil {

    public static ObjectMapper mapper() {
        ObjectMapper mapper = new RESTObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    @SuppressWarnings("unchecked")
    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        return mapper().writeValueAsBytes(object);
    }

    public static <T> T convertTo(Object content, Class<T> tClass) {
        return mapper().convertValue(content, tClass);
    }

    public static <T> T convertTo(ResultActions actions, Class<T> tClass) throws Exception {
        return mapper().readValue(actions.andReturn().getResponse().getContentAsString(), tClass);
    }
}
