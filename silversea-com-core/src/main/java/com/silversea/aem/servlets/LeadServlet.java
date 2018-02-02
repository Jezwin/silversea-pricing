package com.silversea.aem.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletException;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.net.HttpHeaders;
import com.silversea.aem.components.beans.Lead;
import com.silversea.aem.ws.lead.service.LeadService;

@SlingServlet(methods = "POST", paths = "/bin/lead", extensions = "json")
public class LeadServlet extends SlingAllMethodsServlet {

    /**
	 * Default Serial ID.
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(LeadServlet.class);
	private final static String BLACKLIST = "/etc/tags/referrers";
    private static final String CONTENT_APPLICATION_JSON = "application/json";
    private static final String UTF8_ENCODING = "utf-8";

    @Reference
    private LeadService leadService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.sling.api.servlets.SlingAllMethodsServlet#doPost(org.apache.
	 * sling.api.SlingHttpServletRequest,
	 * org.apache.sling.api.SlingHttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
    protected final void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException {
        // Retrieve body content from request
		String leadResponse = StringUtils.EMPTY;
		String body = getBodyFromRequest(request);
		try {

			LOGGER.debug("Lead service request {}", body);
			String referer = "";
			if(request.getCookie("currentReferrer") != null){
				referer = request.getCookie("currentReferrer").getValue();//getHeader(HttpHeaders.REFERER);
			}
			
			if (null != referer && referer != "") {
				LOGGER.debug("The referer obtained here is : - {}", referer);
				/*
				 * Converting the above path into a URL object to obtain the
				 * host name later for black list comparison.
				 */
				URI uri = new URI(referer);
				LOGGER.debug("Assoicated host value here is : - {}", uri.getHost());

				List<String> blockList = ListUtils.EMPTY_LIST;
				Resource blockListResource = request.getResourceResolver().getResource(BLACKLIST);
				if (null != blockListResource) {
					ValueMap blockListMap = blockListResource.getValueMap();
					blockList = Arrays.asList(blockListMap.get("blacklist", String[].class));
					LOGGER.debug("Created black list from mappings under {} and its {}", BLACKLIST, blockList);
					
					if (blockList.contains(uri.getHost())) {
						LOGGER.debug("Match found for {}.", uri.getHost());
						leadResponse = "{\"blockedReferer\":\"" + uri.getHost() + "\"}";
						LOGGER.debug("Lead service response {}", leadResponse);

					} else {
						LOGGER.debug("There is no blocked referer here. Executing normal flow");
						Lead lead = JsonMapper.getDomainObject(body, Lead.class);
						leadResponse = "{\"leadResponse\":\"" + leadService.sendLead(lead) + "\"}";
						LOGGER.debug("Lead service response {}", leadResponse);

					}
				}else{
					//No blocklist found - proceed with the lead
					Lead lead = JsonMapper.getDomainObject(body, Lead.class);
					leadResponse = "{\"leadResponse\":\"" + leadService.sendLead(lead) + "\"}";
					LOGGER.debug("Lead service response {}", leadResponse);
				}

				
				writeDomainObject(response, leadResponse);
			} else {
				LOGGER.debug("There is no blocked referer here. Executing normal flow");
				Lead lead = JsonMapper.getDomainObject(body, Lead.class);
				leadResponse = "{\"leadResponse\":\"" + leadService.sendLead(lead) + "\"}";
				LOGGER.debug("Lead service response {}", leadResponse);
				writeDomainObject(response, leadResponse);
			}
		} catch (URISyntaxException e) {
			LOGGER.debug("Error observed while sending the lead. {} {}",e , e.getMessage());
			//Try to send the lead if pasrsing uri failed
			Lead lead = JsonMapper.getDomainObject(body, Lead.class);
			leadResponse = "{\"leadResponse\":\"" + leadService.sendLead(lead) + "\"}";
			LOGGER.debug("Lead service response {}", leadResponse);
			writeDomainObject(response, leadResponse);
			e.printStackTrace();
		} catch (Exception e){
			LOGGER.debug("Error observed while sending the lead. {} {}",e , e.getMessage());
			//Try to send the lead if pasrsing uri failed
			Lead lead = JsonMapper.getDomainObject(body, Lead.class);
			leadResponse = "{\"leadResponse\":\"" + leadService.sendLead(lead) + "\"}";
			LOGGER.debug("Lead service response {}", leadResponse);
			writeDomainObject(response, leadResponse);
			e.printStackTrace();
		}

    }

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

    public static class JsonMapper {

        private static final Logger LOGGER = LoggerFactory.getLogger(JsonMapper.class);
        static ObjectMapper mapper = new ObjectMapper();

        static {
            mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
            mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            mapper.setDateFormat(df);
        }

        public static String getJson(Object o) {
            try {
                return mapper.writeValueAsString(o);
            } catch (JsonProcessingException e) {
                LOGGER.error("Error during marshalling", e);
                return null;
            }
        }

        public static <T> T getDomainObject(String jsonIn, Class<T> clazz) {
            try {
                return mapper.readValue(jsonIn, clazz);
            } catch (IOException e) {
                LOGGER.error("Error during unmarshalling", e);
                return null;
            }
        }
    }
}
