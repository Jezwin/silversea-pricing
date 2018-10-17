package com.silversea.aem.components.page;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.beans.Lead;
import com.silversea.aem.components.beans.ResubmitLead;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.servlets.LeadServlet.JsonMapper;

/**
 * The file to display the relevant details to be submitted on the notification
 * panel.
 * 
 * @author nikhil
 *
 */
public class NotificationUse extends WCMUsePojo {

	private final static String LEAD_DATA_PATH = "/var/leadservicedata";
	private List<ResubmitLead> leadData;
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationUse.class);
	
	@Override
	public void activate() throws Exception {
		final Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE,
				ImportersConstants.SUB_SERVICE_IMPORT_DATA);
		try (final ResourceResolver adminResolver = getSlingScriptHelper().getService(ResourceResolverFactory.class)
				.getServiceResourceResolver(authenticationParams)) {
			
			if (null != adminResolver) {
				leadData = new ArrayList<ResubmitLead>();
				Node rootNode = adminResolver.getResource(LEAD_DATA_PATH).adaptTo(Node.class);
				NodeIterator childrenNodes = rootNode.getNodes();
				while (childrenNodes.hasNext()) {
					Node next = childrenNodes.nextNode();
					String body = IOUtils.toString(JcrUtils.readFile(next),
							StandardCharsets.UTF_8.name());
					Lead lead = JsonMapper.getDomainObject(body, Lead.class);	
					ResubmitLead resubmitLead = new ResubmitLead();
					resubmitLead.setFileName(next.getName());
					resubmitLead.setType(lead.getRequestsource());
					resubmitLead.setDateOfSubmission(lead.getSubmitDate());
					leadData.add(resubmitLead);
				} 
			}
		} catch (LoginException loginException) {
			LOGGER.error(
					"Exception while retrieving admin access for Resolver: {}",
					loginException.getMessage());
		} catch (RepositoryException repositoryException) {
			LOGGER.error("Repository Exception: {}", repositoryException);
		} 		
		LOGGER.debug("The current lead data is:- {}", leadData);
	}

	public List<ResubmitLead> getLeadData() {
		return leadData;
	}
}
