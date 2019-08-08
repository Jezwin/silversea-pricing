package com.silversea.aem.ws.client.factory;

import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.endpoint.ConduitSelector;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.Conduit;

public class JaxWsClient extends ClientImpl {

    public JaxWsClient(Bus bus, Endpoint endpoint, Conduit conduit) {
        super(bus, endpoint, conduit);
    }

    public JaxWsClient(Bus bus, Endpoint endpoint, ConduitSelector conduitSelector) {
        super(bus, endpoint, conduitSelector);
    }

    public JaxWsClient(Bus bus, Endpoint endpoint) {
        super(bus, endpoint);
    }

    @Override
    public Object[] invoke(BindingOperationInfo bindingOperationInfo, Object[] pParams, Map<String, Object> context, Exchange exchange)
            throws Exception {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(BusFactory.class.getClassLoader());

            return super.invoke(bindingOperationInfo, pParams, context, exchange);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }
}
