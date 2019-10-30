package com.silversea.aem.config;

import org.apache.felix.scr.annotations.*;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

import java.util.Dictionary;

@Component(immediate = true, metatype = true, label = "CoreConfig")
@Service(CoreConfig.class)
@Properties({
        @Property(name = "awsRegion"),
        @Property(name = "awsSecretName"),
        @Property(name = "exclusiveOfferApiDomain")
})
public class CoreConfig {

    private String awsRegion;
    private String awsSecretName;
    private String exclusiveOfferApiDomain;

    @Activate
    protected final void activate(final ComponentContext context) {
        Dictionary<String, String> properties = context.getProperties();
        awsRegion = PropertiesUtil.toString(properties.get("awsRegion"), "us-east-1");
        awsSecretName = PropertiesUtil.toString(properties.get("awsSecretName"), null);
        exclusiveOfferApiDomain = PropertiesUtil.toString(properties.get("exclusiveOfferApiDomain"), "127.0.0.1:3000");
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public String getAwsSecretName() {
        return awsSecretName;
    }

    public String getExclusiveOfferApiDomain() { return exclusiveOfferApiDomain;
    }
}
