package com.noubase.core.crud.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Created by rshuper on 26.11.14.
 * © egocube.com
 */
public class ObjectIdSerializer extends JsonSerializer<ObjectId> {

    @Override
    public void serialize(@NotNull ObjectId value, @NotNull JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(value.toString());
    }


}
