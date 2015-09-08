package com.noubase.core.crud.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.noubase.core.util.TypeUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Created by rshuper on 27.11.14.
 * © egocube.com
 */
public class BooleanDeserializer extends JsonDeserializer<Boolean> {

    @NotNull
    @Override
    public Boolean deserialize(@NotNull JsonParser jp, DeserializationContext context) throws IOException {
        return TypeUtil.convertToBoolean(jp.getValueAsString());
    }
}
