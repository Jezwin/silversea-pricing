package com.silversea.aem.ws.client.factory;

import org.apache.cxf.BusFactory;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class JaxWsClientFactory {
    
private static final Logger LOGGER = LoggerFactory.getLogger(JaxWsClientFactory.class);
    
    private JaxWsClientFactory() {
    
    }

    public static <T> T create(Class<T> pClass, String pPortUrl, String pBinding, String pUsername, String pPassword) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(BusFactory.class.getClassLoader());
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(new ClientFactoryBean());
            factory.setServiceClass(pClass);
            factory.setAddress(pPortUrl);
            factory.setUsername(pUsername);
            factory.setPassword(pPassword);
            factory.setBindingId(pBinding);
            factory.getFeatures().add(new LoggingFeature());
            return (T) factory.create();
        } catch (Exception e) {
            LOGGER.error("Exception while creating jaxws client",e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

        return null;
    }

}
