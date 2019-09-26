package com.silversea.aem.config;

import org.apache.felix.scr.annotations.*;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

import java.util.Dictionary;

@Component(immediate = true, metatype = true, label = "CoreConfig")
@Service(CoreConfig.class)
@Properties({
        @Property(name = "awsRegion"),
        @Property(name = "awsSecretName")
})
public class CoreConfig {

    private String awsRegion;
    private String awsSecretName;

    @Activate
    protected final void activate(final ComponentContext context) {
        Dictionary<String, String> properties = context.getProperties();
        awsRegion = PropertiesUtil.toString(properties.get("awsRegion"), "us-east-1");
        awsSecretName = PropertiesUtil.toString(properties.get("awsSecretName"), null);
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public String getAwsSecretName() {
        return awsSecretName;
    }
}
