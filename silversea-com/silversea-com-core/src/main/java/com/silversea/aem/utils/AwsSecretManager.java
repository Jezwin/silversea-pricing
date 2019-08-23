package com.silversea.aem.utils;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.jasongoodwin.monads.Try;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Optional;

@Component(immediate = true, metatype = true, label = "AwsSecretManager")
@Service(AwsSecretManager.class)
@Properties({
        @Property(name = "region")}
)
public class AwsSecretManager {
    private String region;

    @Activate
    protected final void activate(final ComponentContext context) {
        Dictionary<String,String> properties = context.getProperties();
        region = PropertiesUtil.toString(properties.get("region"), "us-east-1");
    }

    public Try<GetSecretValueResult> getSecretValue(String secretName) {
        return Try.ofFailable(() -> {
            AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard()
                    .withRegion(region)
                    .build();
            GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                    .withSecretId(secretName);
            return client.getSecretValue(getSecretValueRequest);
        });
    }
}
