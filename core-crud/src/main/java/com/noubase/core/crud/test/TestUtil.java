package com.noubase.core.crud.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noubase.core.crud.serialization.RESTObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;

/**
 * Created by rshuper on 24.07.15.
 */
public final class TestUtil {

    @NotNull
    public static ObjectMapper mapper() {
        ObjectMapper mapper = new RESTObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    public static byte[] convertObjectToJsonBytes(@NotNull Object object) throws IOException {
        return mapper().writeValueAsBytes(object);
    }

    public static <T> T convertTo(Object content, Class<T> tClass) {
        return mapper().convertValue(content, tClass);
    }

    public static <T> T convertTo(@NotNull ResultActions actions, @NotNull Class<T> tClass) throws Exception {
        return mapper().readValue(actions.andReturn().getResponse().getContentAsString(), tClass);
    }
}
