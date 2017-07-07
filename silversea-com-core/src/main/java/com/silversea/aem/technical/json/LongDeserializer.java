package com.silversea.aem.technical.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class LongDeserializer extends StdDeserializer <Long>{

    protected LongDeserializer(Class<?> vc) {
        super(vc);
    }
    
    public LongDeserializer() { 
        this(null); 
    } 

    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {        
        Long value  = Long.valueOf(p.getText());
        return value;
    }

}
