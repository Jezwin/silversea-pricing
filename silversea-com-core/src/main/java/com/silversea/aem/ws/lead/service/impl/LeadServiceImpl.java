package com.silversea.aem.ws.lead.service.impl;

import java.util.Dictionary;
import java.util.Locale;
import java.util.Objects;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.cxf.binding.soap.SoapBindingFactory;
import org.apache.cxf.common.util.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

import com.silversea.aem.components.beans.Lead;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.utils.DateUtils;
import com.silversea.aem.ws.LeadFromWeb03Soap;
import com.silversea.aem.ws.NewX0020MethodX0020WithX002052X0020Arguments;
import com.silversea.aem.ws.NewX0020MethodX0020WithX002052X0020ArgumentsResponse;
import com.silversea.aem.ws.client.factory.JaxWsClientFactory;
import com.silversea.aem.ws.lead.service.LeadService;

@Component(immediate = true, metatype = true, label = "Silversea Lead Service")
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
    private static final String PASSWORD = "password";
    private String password;

    @Activate
    protected final void activate(final ComponentContext context) {

        Dictionary<?, ?> properties = context.getProperties();

        url = PropertiesUtil.toString(properties.get(URL), DEFAULT_URL);
        username = PropertiesUtil.toString(properties.get(USERNAME), DEFAULT_USERNAME);
        password = PropertiesUtil.toString(properties.get(PASSWORD), DEFAULT_PASSWORD);
    }

    /** {@inheritDoc}} **/
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
        request.setRequestType(lead.getRequesttype());
        request.setRequestSource(lead.getRequestsource());

        request.setCountry("");
        request.setSiteLanguage("");
        request.setSiteCurrency("");

        request.setTitle(lead.getTitle());
        request.setNameFirst(lead.getFirstname());
        request.setNameLast(lead.getLastname());
        request.setEmail(lead.getEmail());
        request.setPhone(lead.getPhone());
        request.setComments(lead.getComments());

        // Subscribe newsletter
        request.setAtt02(lead.getAtt02());
        request.setSubscribeEmail(lead.getAtt02());
        request.setWorkingWithAgent(lead.getWorkingwithagent());

        // request a quote
        request.setVoyage(lead.getVoyagename());
        request.setVoyageCod(lead.getVoyagecode());
        String departureDate = lead.getDeparturedate();
        if (!StringUtils.isEmpty(departureDate)) {
            XMLGregorianCalendar sailDate = DateUtils.getXmlGregorianCalendar(departureDate, "dd MMM yyyy");
            request.setSailDate(sailDate);
        }
        request.setShip(lead.getShipname());
        request.setAtt07(lead.getSuitecategory());
        request.setAtt08(lead.getSuitevariation());
        request.setAtt09(lead.getPrice());

        // request a brochure
        request.setAddress1(lead.getPostaladdress());
        request.setZip(lead.getPostalcode());
        request.setCity(lead.getCity());
        request.setCountry(lead.getCountry());
        request.setBrochuresRequested("");

    }

    private LeadFromWeb03Soap getClientProxy() {
        return JaxWsClientFactory.create(LeadFromWeb03Soap.class, url, BINDING, username, password);
    }
}