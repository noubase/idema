package com.noubase.idema.mapping;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import javax.validation.constraints.NotNull;
import java.io.IOException;

import static com.noubase.idema.util.TypeUtil.convertToBoolean;

/**
 * Created by rshuper on 27.11.14.
 * © egocube.com
 */
public class BooleanDeserializer extends JsonDeserializer<Boolean> {

    @NotNull
    @Override
    public Boolean deserialize(@NotNull JsonParser jp, DeserializationContext context) throws IOException {
        return convertToBoolean(jp.getValueAsString());
    }
}
