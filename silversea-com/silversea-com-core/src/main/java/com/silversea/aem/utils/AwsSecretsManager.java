package com.silversea.aem.utils;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.jasongoodwin.monads.Try;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;

import java.util.Dictionary;

@Component(immediate = true, metatype = true, label = "AwsSecretManager")
@Service(AwsSecretsManager.class)
@Properties({
    @Property(name = "region"),
    @Property(name = "secretName")
})
public class AwsSecretsManager {
    private String region;
    private String secretName;

    @Activate
    protected final void activate(final ComponentContext context) {
        Dictionary<String, String> properties = context.getProperties();
        this.region = PropertiesUtil.toString(properties.get("region"), "us-east-1");
        this.secretName = PropertiesUtil.toString(properties.get("secretName"), null);
    }

    public Try<String> getValue(String key) {
        return Try.ofFailable(() -> {
            if (this.secretName == null) throw new Exception("AWS secretName not set in configuration.");
            JSONObject secret = fetchSecret(this.region, this.secretName);
            return secret.getString(key);
        });
    }

    private static JSONObject fetchSecret(String region, String secretName) throws JSONException {
        AWSSecretsManager client = buildClient(region);
        GetSecretValueRequest request = buildRequest(secretName);
        GetSecretValueResult secretValue = client.getSecretValue(request);
        String jsonString = secretValue.getSecretString();
        return new JSONObject(jsonString);
    }

    private static GetSecretValueRequest buildRequest(String secretName) {
        return new GetSecretValueRequest()
                .withSecretId(secretName);
    }

    private static AWSSecretsManager buildClient(String region) {
        return AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .build();
    }
}
