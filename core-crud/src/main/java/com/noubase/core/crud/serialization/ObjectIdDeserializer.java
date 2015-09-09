package com.noubase.core.crud.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


/**
 * Created by rshuper on 27.11.14.
 * © egocube.com
 */
public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {

    @NotNull
    @Override
    public ObjectId deserialize(@NotNull JsonParser jp, DeserializationContext context) throws IOException {
        return new ObjectId(jp.getValueAsString());
    }
}
