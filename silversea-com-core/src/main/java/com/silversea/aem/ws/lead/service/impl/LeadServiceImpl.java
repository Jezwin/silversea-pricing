package com.silversea.aem.ws.lead.service.impl;

import java.util.Dictionary;
import java.util.Map;
import java.util.Objects;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;

import org.apache.cxf.binding.soap.SoapBindingFactory;
import org.apache.cxf.common.util.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

import com.silversea.aem.components.beans.Lead;
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
            BindingProvider bindingProvider = (BindingProvider) leadFromWeb03Soap;            
            Map<String, Object> requestContext = bindingProvider.getRequestContext();
            requestContext.put("javax.xml.ws.client.connectionTimeout",10000);
            requestContext.put("javax.xml.ws.client.receiveTimeout",10000);
        
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

        request.setSiteCountry(lead.getSitecountry());
        request.setSiteLanguage(lead.getSitelanguage());
        request.setSiteCurrency(lead.getSitecurrency());

        request.setTitle(lead.getTitle());
        request.setNameFirst(lead.getFirstname());
        request.setNameLast(lead.getLastname());
        request.setEmail(lead.getEmail());
        request.setSubscribeEmail(lead.getSubscribeemail());
        request.setPhone(lead.getPhone());
        request.setComments(lead.getComments());

        // Subscribe newsletter
        request.setSubscribeEmail(lead.getSubscribeemail());
        if (!StringUtils.isEmpty(lead.getWorkingwithagent())) {
            request.setWorkingWithAgent(Short.parseShort("1")); //true
        }


        // request a quote
        request.setVoyage(lead.getVoyagename());
        request.setVoyageCod(lead.getVoyagecode());
        String departureDate = lead.getDeparturedate();
        if (!StringUtils.isEmpty(departureDate)) {
            XMLGregorianCalendar sailDate = DateUtils.getXmlGregorianCalendar(departureDate, "dd MMM yyyy");
            request.setSailDate(sailDate);
        }
        if(!StringUtils.isEmpty(lead.getShipname())) {
            request.setShip(lead.getShipname());
        }
        request.setSuiteCategory(lead.getSuitecategory());
        request.setSuiteVariation(lead.getSuitevariation());
        if (!StringUtils.isEmpty(lead.getPriceString())) {
            request.setPrice(lead.getPrice());
        }

        // request a brochure
        request.setAddress1(lead.getPostaladdress());
        request.setZip(lead.getPostalcode());
        request.setCity(lead.getCity());
        request.setCountry(lead.getCountry());
        request.setState(lead.getState());
        request.setBrochuresRequested(lead.getBrochurecode());
        if (lead.getRequestsource() != null && (lead.getRequestsource().equals("BRO") || lead.getRequestsource().equals("EBRO"))) {
            if (lead.getIsnotagent() != null && lead.getIsnotagent().equals("true")) {
                request.setIsAgent(Short.parseShort("0")); //false - is agent
            } else {
                request.setIsAgent(Short.parseShort("1")); //true - is agent
            }
        }

        // Marketing
        request.setMarketingEffort(lead.getMarketingEffort());
        //MCId
        request.setMCId(lead.getMcid());
        //Last URL
        request.setAtt02(lead.getAtt02());
        //EO Preference
        request.setAtt01(lead.getAtt01());
        if(!StringUtils.isEmpty(lead.getShip())) {
            request.setShip(lead.getShip());
        }
        request.setPreferredDestinations(lead.getPreferredDestinations());
    }

    private LeadFromWeb03Soap getClientProxy() {
        return JaxWsClientFactory.create(LeadFromWeb03Soap.class, url, BINDING, username, password);
    }
}
