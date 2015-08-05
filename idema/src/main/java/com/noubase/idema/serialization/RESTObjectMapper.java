package com.noubase.idema.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.noubase.idema.model.BooleanDeserializer;

/**
 * © 13.02.15 rshuper
 */
@SuppressWarnings("serial")
public class RESTObjectMapper extends ObjectMapper {
    public RESTObjectMapper() {
        SimpleModule module = new SimpleModule("JSONModule", new Version(2, 0, 0, null, null, null));
        module.addDeserializer(Boolean.class, new BooleanDeserializer());
        this.registerModule(module);

        super.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        super.setConfig(super.getSerializationConfig().withView(Public.class));
    }
}