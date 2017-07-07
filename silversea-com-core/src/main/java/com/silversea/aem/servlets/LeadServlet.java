package com.silversea.aem.servlets;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.components.beans.Lead;
import com.silversea.aem.technical.json.JsonMapper;
import com.silversea.aem.ws.lead.service.LeadService;

@SlingServlet(methods = "POST", resourceTypes = "sling/servlet/default", selectors = "lead", extensions = "json")
public class LeadServlet extends ScServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(LeadServlet.class);

    private static final long serialVersionUID = 1L;

    @Reference
    private LeadService leadService;

    protected final void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException {
        // Retrieve body content from request
        String body = getBodyFromRequest(request);
        LOGGER.debug("Lead service request {}", body);
        Lead lead = JsonMapper.getDomainObject(body, Lead.class);
        String leadResponse = leadService.sendLead(lead);
        LOGGER.debug("Lead service response {}", response);
        writeDomainObject(response, leadResponse);
    }
}