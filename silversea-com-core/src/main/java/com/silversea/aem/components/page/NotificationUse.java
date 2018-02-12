package com.silversea.aem.components.page;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

/**
 * The file to display the relevant details to be submitted on the notification
 * panel.
 * 
 * @author nikhil
 *
 */
public class NotificationUse extends WCMUsePojo {

	private final static String LEAD_DATA_PATH = "/var/leadservicedata";
	private List<String> leadData;
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationUse.class);
	
	@Override
	public void activate() throws Exception {
		leadData = new ArrayList<String>();
		Node rootNode = getResourceResolver().getResource(LEAD_DATA_PATH).adaptTo(Node.class);
		
		NodeIterator childrenNodes = rootNode.getNodes();

		while (childrenNodes.hasNext()) {
			Node next = childrenNodes.nextNode();
			leadData.add(next.getName());
		}
		LOGGER.debug("The current lead data is:- {}", leadData);
	}

	public List<String> getLeadData() {
		return leadData;
	}
}
