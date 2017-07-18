package com.silversea.aem.technical.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class ArrayDeserializer extends StdDeserializer <String[]>{

    protected ArrayDeserializer(Class<?> vc) {
        super(vc);
    }
    
    public ArrayDeserializer() { 
        this(null); 
    } 

    @Override
    public String[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature. ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return mapper.readValue(p, String[].class);
    }

}
