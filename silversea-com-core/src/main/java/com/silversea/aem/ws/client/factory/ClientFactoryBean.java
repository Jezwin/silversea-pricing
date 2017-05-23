package com.silversea.aem.ws.client.factory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.JaxWsClientFactoryBean;

public class ClientFactoryBean extends JaxWsClientFactoryBean{
    
    @Override
    protected Client createClient(Endpoint endpoint) {
        return new JaxWsClient(getBus(), endpoint, getConduitSelector());
    }

}
