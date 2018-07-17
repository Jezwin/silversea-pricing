package com.silversea.aem.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.xml.ws.WebServiceException;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.silversea.aem.components.beans.Lead;
import com.silversea.aem.utils.LeadUtils;
import com.silversea.aem.ws.lead.service.LeadService;

@SlingServlet(methods = "POST", paths = "/bin/lead", extensions = "json")
public class LeadServlet extends SlingAllMethodsServlet {

    /**
	 * Default Serial ID.
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(LeadServlet.class);
	private final static String BLACKLIST = "/etc/tags/referrers";
    
    @Reference
	private ResourceResolverFactory resourceResolverFactory;
    private static final String[] IP_HEADER_CANDIDATES = { 
    	    "X-Forwarded-For",
    	    "Proxy-Client-IP",
    	    "WL-Proxy-Client-IP",
    	    "HTTP_X_FORWARDED_FOR",
    	    "HTTP_X_FORWARDED",
    	    "HTTP_X_CLUSTER_CLIENT_IP",
    	    "HTTP_CLIENT_IP",
    	    "HTTP_FORWARDED_FOR",
    	    "HTTP_FORWARDED",
    	    "HTTP_VIA",
    	    "REMOTE_ADDR" };

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
	protected final void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException {
			sendLead(request, response);
    }
	
	private void sendLead(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		// Retrieve body content from request
				String leadResponse = StringUtils.EMPTY;
				Resource blockListResource = request.getResourceResolver().getResource(BLACKLIST);
				String body = getBodyFromRequest(request);
				try {

					LOGGER.debug("Lead service request {}", body);
					String referer = "";
					String ipaddress = getClientIpAddress(request);
					String emailadress = getEmailAddress(body);
					if(request.getCookie("currentReferrer") != null){
						referer = request.getCookie("currentReferrer").getValue();//getHeader(HttpHeaders.REFERER);
					}
					
					if (null != blockListResource) {
						if((null != referer && referer != "") || (null != ipaddress && ipaddress != "") || 
								(null != emailadress && emailadress != "")) {	
							if (null != referer && referer != "") {
								LOGGER.debug("The referer obtained here is : - {}", referer);
								/*
								 * Converting the above path into a URL object to obtain the
								 * host name later for black list comparison.
								 */
								URI uri = new URI(referer);
								LOGGER.debug("Assoicated host value here is : - {}", uri.getHost());
				
								List<String> blockList = ListUtils.EMPTY_LIST;
								
								ValueMap blockListMap = blockListResource.getValueMap();
								blockList = Arrays.asList(blockListMap.get("blacklist", String[].class));
								LOGGER.debug("Created black list from mappings under {} and its {}", BLACKLIST, blockList);
									
								if (blockList.contains(uri.getHost())) {
									LOGGER.debug("Match found for {}.", uri.getHost());
									leadResponse = "{\"blockedReferer\":\"" + uri.getHost() + "\"}";
									LOGGER.debug("Lead service response {}", leadResponse);	
									}
							} 
							
							if (null != ipaddress && ipaddress != "") {
								LOGGER.debug("The ipaddress obtained here is : - {}", ipaddress);

								List<String> ipBlockList = ListUtils.EMPTY_LIST;
								ValueMap ipBlockListMap = blockListResource.getValueMap();
								ipBlockList = Arrays.asList(ipBlockListMap.get("ipblacklist", String[].class));
								LOGGER.debug("Created ip black list from mappings under {} and its {}", BLACKLIST, ipBlockList);
							
								if (ipBlockList.contains(ipaddress)) {
									LOGGER.debug("Match found for {}.", ipaddress);
									leadResponse = "{\"blockedReferer\":\"" + ipaddress + "\"}";
									LOGGER.debug("Lead service response {}", leadResponse);
								}
							}  
							
							if (null != emailadress && emailadress != "") {
								LOGGER.debug("The emailadress obtained here is : - {}", emailadress);

								List<String> emailBlockList = ListUtils.EMPTY_LIST;
								ValueMap emailBlockListMap = blockListResource.getValueMap();
								emailBlockList = Arrays.asList(emailBlockListMap.get("emailblacklist", String[].class));
								LOGGER.debug("Created email black list from mappings under {} and its {}", BLACKLIST, emailBlockList);
							
								if (emailBlockList.contains(emailadress)) {
									LOGGER.debug("Match found for {}.", emailadress);
									leadResponse = "{\"blockedReferer\":\"" + emailadress + "\"}";
									LOGGER.debug("Lead service response {}", leadResponse);
								} else {
									List<String> domainBlockList = ListUtils.EMPTY_LIST;
									ValueMap domainBlockListMap = blockListResource.getValueMap();
									domainBlockList = Arrays.asList(domainBlockListMap.get("domainblacklist", String[].class));
									LOGGER.info("Created domain black list from mappings under {} and its {}", BLACKLIST, domainBlockList);
									if(domainBlockList.contains(emailadress.substring(emailadress.indexOf("@") + 1))) {
										LOGGER.info("Match found for {}.", emailadress);
										leadResponse = "{\"blockedReferer\":\"" + emailadress + "\"}";
										LOGGER.info("Lead service response {}", leadResponse);
									}
								}
							} 
							
							if(leadResponse != StringUtils.EMPTY ) {
								LeadUtils.writeDomainObject(response, leadResponse);
							} else {
								LOGGER.debug("There is no blocked referer , ipaddress or emailadress here. Executing normal flow");
								writeLeadResponse(request, response, body);
							}
						} else {
						LOGGER.debug("There is no blacklisted referer , ipaddress or emailadress here. Executing normal flow");
						writeLeadResponse(request, response, body);
						}
					} else {
						writeLeadResponse(request, response, body);
					}
				} catch (URISyntaxException e) {
					LOGGER.debug("Error observed while sending the lead. {} {}",e , e.getMessage());
					writeLeadResponse(request, response, body);
					e.printStackTrace();
				} catch (Exception e){
					LOGGER.debug("Error observed while sending the lead. {} {}",e , e.getMessage());
					writeLeadResponse(request, response, body);
					e.printStackTrace();
				}

		
	}
	/**
	 * Wrapper method to write the lead response into a domain object.
	 * 
	 * @param request
	 *            The obtained request.
	 * @param response
	 *            The sent response
	 * @param body
	 *            The data JSON.
	 */
	private void writeLeadResponse(final SlingHttpServletRequest request, final SlingHttpServletResponse response, String body) {
		String leadResponse = StringUtils.EMPTY;
		Lead lead = JsonMapper.getDomainObject(body, Lead.class);
		try{
			leadResponse = "{\"leadResponse\":\"" + leadService.sendLead(lead) + "\"}";
		} catch (WebServiceException e) {
			LOGGER.debug("Lead service request {}", e);
			String tempId = StringUtils.EMPTY;
			try {
				tempId = LeadUtils.generateLeadDataFile(resourceResolverFactory, request, body, getEmailAddress(body));
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e1) {
				LOGGER.error("The temporary ID couldn't be generated. Please check your settings. {} {}", e,
						e.getMessage());
			}
			leadResponse = "{\"temporaryId\":\"" + tempId + "\"}";
		} 
		finally{
			LOGGER.debug("Lead service response {}", leadResponse);
			LeadUtils.writeDomainObject(response, leadResponse);
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
     * @param body from request
     * @return email
     *               email address in String format
     */
    protected String getEmailAddress(String body) {
    	Lead leadObject = JsonMapper.getDomainObject(body, Lead.class);
    	return leadObject.getEmail();
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
    
    /**
     * Return IP address of the request origin
     * @param request
     *              Http request
     * @return ip
     *             IP address in string fromat
     */
    public static String getClientIpAddress(SlingHttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
