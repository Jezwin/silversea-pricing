package com.silversea.aem.ws.lead.service.impl;

import java.util.Dictionary;
import java.util.Objects;

import org.apache.cxf.binding.soap.SoapBindingFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

import com.silversea.aem.components.beans.Lead;
import com.silversea.aem.ws.LeadFromWeb03Soap;
import com.silversea.aem.ws.NewX0020MethodX0020WithX002052X0020Arguments;
import com.silversea.aem.ws.NewX0020MethodX0020WithX002052X0020ArgumentsResponse;
import com.silversea.aem.ws.client.factory.JaxWsClientFactory;
import com.silversea.aem.ws.lead.service.LeadService;

@Component(immediate = true, metatype = false, label = "Silversea Lead Service")
@Service(LeadService.class)
public class LeadServiceImpl implements LeadService {

    private static final String DEFAULT_URL = "http://servicest.silversea.com/LeadFromWeb03.asmx";
    private static final String DEFAULT_USERNAME = "Agency1002015";
    private static final String DEFAULT_PASSWORD = "Th1nk2015Test";

    private static final String BINDING = SoapBindingFactory.SOAP_11_BINDING;

    @Property(label = "Webservice Lead url", value = DEFAULT_URL)
    private static final String URL = "url";
    private String url;

    @Property(label = "Webservice Lead username", value = DEFAULT_USERNAME)
    private static final String USERNAME = "username";
    private String username;

    @Property(label = "Webservice Lead password", value = DEFAULT_PASSWORD)
    private static final String PASSWORD = DEFAULT_PASSWORD;
    private String password;

    @Activate
    protected final void activate(final ComponentContext context) {

        Dictionary<?, ?> properties = context.getProperties();

        url = PropertiesUtil.toString(properties.get(URL), DEFAULT_URL);
        username = PropertiesUtil.toString(properties.get(USERNAME), DEFAULT_USERNAME);
        password = PropertiesUtil.toString(properties.get(PASSWORD), DEFAULT_PASSWORD);
    }

    /**{@inheritDoc}}**/
    @Override
    public String sendLead(Lead lead) {

        String id = null;

        if (lead != null) {
            NewX0020MethodX0020WithX002052X0020Arguments request = new NewX0020MethodX0020WithX002052X0020Arguments();
            LeadFromWeb03Soap leadFromWeb03Soap = getClientProxy();
            adaptLeadRequest(request, lead);
            NewX0020MethodX0020WithX002052X0020ArgumentsResponse response = leadFromWeb03Soap.addRequest(request);

            if (response != null) {
                id = Objects.toString(response.getNewX0020MethodX0020WithX002052X0020ArgumentsResult());
            }
        }

        return id;
    }

    public void adaptLeadRequest(NewX0020MethodX0020WithX002052X0020Arguments request, Lead lead) {
        request.setTitle(lead.getTitle());
        request.setNameFirst(lead.getFirstname());
        request.setNameLast(lead.getLastname());
        request.setEmail(lead.getEmail());
        request.setPhone(lead.getPhone());
        request.setComments(lead.getDescription());
    }

    private LeadFromWeb03Soap getClientProxy() {
        return JaxWsClientFactory.create(LeadFromWeb03Soap.class, url, BINDING, username, password);
    }

}
