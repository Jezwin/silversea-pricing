package com.silversea.aem.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.xml.ws.WebServiceException;

import org.apache.commons.io.IOUtils;
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

import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.Lead;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.servlets.LeadServlet.JsonMapper;
import com.silversea.aem.utils.LeadUtils;
import com.silversea.aem.ws.lead.service.LeadService;

@SlingServlet(methods = "GET", paths = "/bin/phoneCustom", extensions = "json")
public class PhoneCustomServlet extends SlingAllMethodsServlet {
	/**
	 * Default serial id.
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(PhoneCustomServlet.class);
	
	private static String PATH_TAGS_PHONE = "/etc/tags/phone";
	private static final String CONTENT_APPLICATION_JSON = "application/json";
	private static final String UTF8_ENCODING = "utf-8";
	
	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	protected final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException {
		fetchPhone(request, response);
	}


	private void fetchPhone(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
		LOGGER.debug("Starting Phone Fetch..");
		final Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		try (final ResourceResolver rsrResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			
			Resource resource = rsrResolver.getResource(PATH_TAGS_PHONE);
			HashMap<String,String> result = new HashMap();
			
			if(resource != null) {
				String requestedCountry = request.getParameter("country").toString();
				if(requestedCountry != "") {
					Resource countryResource = resource.getChild(requestedCountry);
					if(countryResource != null) {
						ValueMap countryMapValues = countryResource.getValueMap();
						//foreach only if contain src- (send back without)
						
						for (Map.Entry<String, Object> entry : countryMapValues.entrySet()) {
							if(entry.getKey().contains("src-")) {
								result.put(entry.getKey().replace("src-", ""), entry.getValue().toString());
							}
						}
					}
				}	
			}
						
			response.setContentType(CONTENT_APPLICATION_JSON);
			response.setCharacterEncoding(UTF8_ENCODING);
			String json = JsonMapper.getJson(result);

			try {
				response.getWriter().write(json);
			} catch (IOException e) {
				LOGGER.error("Error while writing json in the response", e);
			}

		} catch (Exception e) {
			LOGGER.error("Exception occured while Phone Fetching", e.getMessage());
		}
		LOGGER.debug("Ënding Phone Fetch...");
	}
}
