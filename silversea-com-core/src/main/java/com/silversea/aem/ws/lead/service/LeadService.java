package com.silversea.aem.ws.lead.service;

import com.silversea.aem.components.beans.Lead;

public interface LeadService {

    /**
     * Send a lead
     * @param lead : lead properties
     * @return response: generated id
     */
    String sendLead(Lead lead);
}
