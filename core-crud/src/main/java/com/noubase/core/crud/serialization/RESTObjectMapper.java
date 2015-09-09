package com.noubase.core.crud.serialization;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;

/**
 * © 13.02.15 rshuper
 */
@SuppressWarnings("serial")
public class RESTObjectMapper extends ObjectMapper {
    public RESTObjectMapper() {
        super();
        SimpleModule module = new SimpleModule("JSONModule", new Version(2, 0, 0, null, null, null));
        module.addSerializer(ObjectId.class, new ObjectIdSerializer());
        module.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        module.addDeserializer(Boolean.class, new BooleanDeserializer());
        this.registerModule(module);
    }
}
