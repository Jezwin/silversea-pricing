package com.silversea.aem.servlets;

import com.silversea.aem.components.beans.Lead;
import com.silversea.aem.technical.json.JsonMapper;
import com.silversea.aem.ws.lead.service.LeadService;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;

@SlingServlet(methods = "POST", paths = "/bin/lead", extensions = "json")
public class LeadServlet extends ScServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeadServlet.class);

    @Reference
    private LeadService leadService;

    protected final void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException {
        // Retrieve body content from request
        String body = getBodyFromRequest(request);

        LOGGER.debug("Lead service request {}", body);

        Lead lead = JsonMapper.getDomainObject(body, Lead.class);
        String leadResponse = leadService.sendLead(lead);

        LOGGER.debug("Lead service response {}", leadResponse);

        writeDomainObject(response, leadResponse);
    }
}