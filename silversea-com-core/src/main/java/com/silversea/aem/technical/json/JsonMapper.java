package com.silversea.aem.technical.json;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonMapper.class);
	static ObjectMapper mapper = new ObjectMapper();
	
	static {
		mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);	
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		mapper.setDateFormat(df);
	}
	
	public static String getJson(Object o){
		try {
			return mapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error during marshalling",e);
			return null;
		}
		
	}
	
	public static <T> T getDomainObject(String jsonIn, Class<T> clazz){
		try {
			return mapper.readValue(jsonIn, clazz);
		} catch (IOException e) {
			LOGGER.error("Error during unmarshalling",e);
			return null;
		}
	}
}
