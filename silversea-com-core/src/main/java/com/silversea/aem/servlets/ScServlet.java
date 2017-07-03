package com.silversea.aem.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.technical.json.JsonMapper;
/**
 * TODO naming
 */
@SuppressWarnings("unchecked")
public class ScServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    
    private static final  Logger LOGGER = LoggerFactory.getLogger(ScServlet.class);
    private static final String CONTENT_APPLICATION_JSON = "application/json";
    private static final String UTF8_ENCODING = "utf-8";
   
    /**
     * Serialize object to json and send it in the response
     * @param response
     *                 Http response
     * @param domainObject
     *                    Object  to send in the response
     */
    protected <T> void writeDomainObject(SlingHttpServletResponse response, T domainObject) {
        response.setContentType(CONTENT_APPLICATION_JSON);
        response.setCharacterEncoding(UTF8_ENCODING);
        String json = JsonMapper.getJson(domainObject);
        
        try {
            response.getWriter().write(json);
        } catch (IOException e) {
            LOGGER.error("Error while writing json in the response", e);
        }
    }

    /**
     * Read body from request 
     * @param request
     *               Http request
     * @return result
     *               Body response in String format
     */
    protected String getBodyFromRequest(SlingHttpServletRequest request) {
        String result = null;

        try {
            BufferedReader reader = request.getReader();

            String line = null;
            StringBuilder buff = new StringBuilder();

            do {
                line = reader.readLine();
                if (line != null) {
                    buff.append(line.trim());
                }
            } while (line != null);
            reader.close();
            result = buff.toString().trim();
        } catch (IOException e) {
            LOGGER.error("Error while reading body response", e);
        }

        return result;
    }
    
    /**
     * Serialize query parameters to json
     * @param request
     *              Http request
     * @return json
     *             request parameters in json fromat
     */
    protected <T> String geParametersFromRequest(SlingHttpServletRequest request){
        Map<?,?> parameters = request.getParameterMap();
        String json = JsonMapper.getJson(formatParameters(parameters));
        return json;
    }
    
    /**
     * Format query String map
     * @param parameters: map of query param
     * @return formatted map
     */
    private <T> Map<String,T> formatParameters(Map<?,?> parameters){
        Map<String,T> map = new HashMap<String,T>();
        if(parameters != null && !parameters.isEmpty()){
            parameters.forEach((k,v)->{
                T value = null;
                String[] array = (String[])v;

                if(ArrayUtils.getLength(array) < 2){
                    value = (T) array[0];
                }
                else{
                    value = (T) array;
                }
                map.put(Objects.toString(k), value);
            });
        }
        return map;
    }
}
