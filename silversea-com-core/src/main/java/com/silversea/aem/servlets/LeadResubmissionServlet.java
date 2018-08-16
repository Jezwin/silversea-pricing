package com.silversea.aem.servlets;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.components.beans.Lead;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.servlets.LeadServlet.JsonMapper;
import com.silversea.aem.utils.LeadUtils;
import com.silversea.aem.ws.lead.service.LeadService;

/**
 * This class is for re-submitting the failed leads from via the notification
 * console.
 * 
 * @author nikhil
 *
 */
@SlingServlet(methods = "POST", paths = "/bin/resubmitlead", extensions = "json")
public class LeadResubmissionServlet extends SlingAllMethodsServlet {
	/**
	 * Default serial id.
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(LeadResubmissionServlet.class);
	private final static String LEAD_DATA_PATH = "/var/leadservicedata";
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	@Reference
	private LeadService leadService;

	private Map<String, List<Map<String, String>>> finalLeadResponse = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.sling.api.servlets.SlingAllMethodsServlet#doPost(org.apache.
	 * sling.api.SlingHttpServletRequest,
	 * org.apache.sling.api.SlingHttpServletResponse)
	 */
	protected final void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException {
		reSubmit(request, response);
	}

	/**
	 * The method designed to resubmit the lead for the failed files. This is
	 * triggered from the designed console. Responsibilities include: - 1.
	 * Handling bulk re-submission requests. 2. Creating a data structure to
	 * hold the status and display the associated lead ids.
	 * 
	 * @param request
	 *            The obtained request.
	 * @param response
	 *            The response to be sent.
	 */
	private void reSubmit(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
		LOGGER.debug("Starting Resubmission..");
		final Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		try (final ResourceResolver adminResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			final Session adminSession = adminResolver.adaptTo(Session.class);

			if (adminSession == null) {
				throw new Exception("Cannot initialize session");
			}
			Resource resource = adminResolver.getResource(LEAD_DATA_PATH);
			Iterator<Resource> resItr = resource.listChildren();
			// data structure
			List<Map<String, String>> responseList = new ArrayList<>();
			while (resItr.hasNext()) {
				Map<String, String> responseMap = new HashMap<>();
				Resource childRes = resItr.next();
				String leadResponse = StringUtils.EMPTY;
				String body = IOUtils.toString(JcrUtils.readFile(childRes.adaptTo(Node.class)),
						StandardCharsets.UTF_8.name());
				LOGGER.debug("The read response is : - {}", body);
				Lead lead = JsonMapper.getDomainObject(body, Lead.class);
				try {
					responseMap.put("filename", childRes.getName());
					leadResponse = leadService.sendLead(lead);
					responseMap.put("responsecode", leadResponse);
				} catch (WebServiceException e) {
					LOGGER.debug(
							"Not able to submit the lead corresponding to the resource {}. Continuing to the next one.",
							childRes.getPath());
					responseMap.put("responsecode", leadResponse);
					responseMap.put("status", "failure");
					responseList.add(responseMap);
					continue;
				}
				if (!StringUtils.isEmpty(leadResponse)) {
					// Log statement to record relation between temporary id and api_indiv_id
					LOGGER.info("Resubmission successful for temporary id {} and the new api_indiv_id received is  {}", childRes.getName().substring(0, childRes.getName().length() - 4),
							leadResponse);
					LOGGER.debug("Resubmission successful for {} and the response is {}", childRes.getPath(),
							leadResponse);
					responseMap.put("status", "success");
					responseList.add(responseMap);
					LOGGER.debug("Deleting the child resource..");
					adminResolver.delete(childRes);
					adminResolver.commit();
				} 
			}
			finalLeadResponse.put("leadResponse", responseList);
			LOGGER.debug("The data structure for the re-submission status is :- {}", finalLeadResponse);
			LeadUtils.writeDomainObject(response, finalLeadResponse);

		} catch (Exception e) {
			LOGGER.error("Exception occured while resubmitting from the console. "
					+ "Would not delete the file from under /var/leadservicedata. "
					+ "You can try submitting again later from the console.", e.getMessage());
		}
		LOGGER.debug("Ënding re-submission...");
	}
}
