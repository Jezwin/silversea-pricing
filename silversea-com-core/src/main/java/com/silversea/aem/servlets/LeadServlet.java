package com.silversea.aem.servlets;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.xml.ws.WebServiceException;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.DamConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.silversea.aem.components.beans.Lead;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.utils.DateUtils;
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
    private final static String LEAD_DATA_PATH = "/var/leadservicedata";
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

		boolean reSubmitFlag = false;
		reSubmitFlag = Boolean.valueOf(request.getParameter("reSubmit"));
		LOGGER.debug("The re-submit flag obtained here is: - {}", reSubmitFlag);
		if (reSubmitFlag) {
			reSubmit(request, response);
		} else {
			sendLead(request, response);
		}
        
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
								}
							} 
							
							if(leadResponse != StringUtils.EMPTY ) {
								writeDomainObject(response, leadResponse);
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
	 * This method is used ot generate the lead data file under /var.
	 * 
	 * @param request
	 *            The obtained request parameter.
	 * @param body
	 *            The json body.
	 */
	private void generateLeadDataFile(final SlingHttpServletRequest request,
			String body) {
		LOGGER.debug("Starting file generation");
		final Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE,
				ImportersConstants.SUB_SERVICE_IMPORT_DATA);
		try (final ResourceResolver adminResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			final Session adminSession = adminResolver.adaptTo(Session.class);
			if (adminSession == null) {
				throw new Exception("Cannot initialize session");
			}
			String fileName = DateUtils.formatDate("yyyyMMddHHmmss'.txt'",
					new Date());
			String path = LEAD_DATA_PATH + "/" + fileName;
			LOGGER.debug("Generating file : - {} with data :- {}", path, body);

			InputStream inputStream = new ByteArrayInputStream(
					body.getBytes(StandardCharsets.UTF_8));
			LOGGER.debug("Setting file metadata..");
			if (adminSession.nodeExists(path + "/" + JcrConstants.JCR_CONTENT)) {
				Node node = adminSession.getNode(path + "/"
						+ JcrConstants.JCR_CONTENT);
				node.setProperty(JcrConstants.JCR_DATA, adminSession
						.getValueFactory().createBinary(inputStream));
				node.setProperty(JcrConstants.JCR_LASTMODIFIED,
						Calendar.getInstance());
				node.setProperty(JcrConstants.JCR_LAST_MODIFIED_BY, node
						.getSession().getUserID());
			} else {
				LOGGER.debug("jcr:content not present. Creating it...");
				Node feedNode = JcrUtil.createPath(path, true,
						DamConstants.NT_SLING_ORDEREDFOLDER,
						JcrConstants.NT_FILE, adminSession, false);
				Node dataNode = feedNode.addNode(JcrConstants.JCR_CONTENT,
						JcrConstants.NT_RESOURCE);
				dataNode.setProperty(JcrConstants.JCR_MIMETYPE, "text/plain");
				dataNode.setProperty(JcrConstants.JCR_ENCODING, UTF8_ENCODING);
				dataNode.setProperty(JcrConstants.JCR_DATA, adminSession
						.getValueFactory().createBinary(inputStream));
			}
			if (adminSession.isLive() || adminSession.hasPendingChanges()) {
				adminSession.save();
			}
		} catch (LoginException loginException) {
			LOGGER.error(
					"Exception while retrieving admin access for Resolver: {}",
					loginException.getMessage());
		} catch (RepositoryException repositoryException) {
			LOGGER.error("Repository Exception: {}", repositoryException);
		} catch (Exception e) {
			LOGGER.error("Exception ", e.getMessage());
		}
	}

	/**
	 * The method designed to resubmit the lead for the failed files. This is
	 * triggered from the designed console.
	 * 
	 * @param request
	 *            The obtained request.
	 * @param response
	 *            The response to be sent.
	 */
	private void reSubmit(final SlingHttpServletRequest request,
			final SlingHttpServletResponse response) {
		LOGGER.debug("Starting Resubmission..");
		final Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE,
				ImportersConstants.SUB_SERVICE_IMPORT_DATA);
		
		try (final ResourceResolver adminResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			final Session adminSession = adminResolver.adaptTo(Session.class);

			if (adminSession == null) {
				throw new Exception("Cannot initialize session");
			}
			Resource resource = adminResolver.getResource(LEAD_DATA_PATH);
			Iterator<Resource> resItr = resource.listChildren();
			while (resItr.hasNext()) {
				Resource childRes = resItr.next();
				String leadResponse = StringUtils.EMPTY;
				String body = IOUtils.toString(
						JcrUtils.readFile(childRes.adaptTo(Node.class)),
						StandardCharsets.UTF_8.name());
				LOGGER.debug("The read response is : - {}", body);
				Lead lead = JsonMapper.getDomainObject(body, Lead.class);
				leadResponse = leadService.sendLead(lead);
				if (!StringUtils.isEmpty(leadResponse)) {
					LOGGER.debug("Resubmission successful for {} and the response is {}", childRes.getPath(), leadResponse);
					LOGGER.debug("Deleting the child resource..");
					adminResolver.delete(childRes);
					adminResolver.commit();
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception occured while resubmitting from the console. "
					+ "Would not delete the file from under /var/leadservicedata. "
					+ "You can try submitting again later from the console.", e.getMessage());
		}
		LOGGER.debug("Ënding re-submission...");
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
			generateLeadDataFile(request, body);
		} finally {
			LOGGER.debug("Lead service response {}", leadResponse);
			writeDomainObject(response, leadResponse);
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
