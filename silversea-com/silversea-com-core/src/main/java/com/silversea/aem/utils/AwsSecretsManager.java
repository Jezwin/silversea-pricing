package com.silversea.aem.utils;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.AWSSecretsManagerException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import io.vavr.control.Try;
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
        return Try.of(() -> {
            if (this.secretName == null) throw new Exception("AWS secretName not set in configuration.");
            JSONObject secret = fetchSecret(this.region, this.secretName);
            return secret.getString(key);
        });
    }

    private static JSONObject fetchSecret(String region, String secretName) throws Exception {
        AWSSecretsManager client = buildClient(region);
        GetSecretValueRequest request = buildRequest(secretName);
        try {
            GetSecretValueResult secretValue = client.getSecretValue(request);
            String jsonString = secretValue.getSecretString();
            return new JSONObject(jsonString);
        } catch (ResourceNotFoundException e) {
            throw new Exception(
                    "AWS SecretsManager returned NotFound for: " + secretName + ".\n" +
                            "If the secret exists, this probably means your AWS CLI credentials (~/.aws/credentials), or the AWS_PROFILE environment variable is referring to the wrong IAM user.", e);
        } catch (AWSSecretsManagerException e) {
            if (e.getErrorCode().equalsIgnoreCase("AccessDeniedException"))
                throw new Exception(
                        "AWS SecretsManager returned AccessDenied for: " + secretName + ".\n" +
                                "Maybe your AWS CLI credentials (~/.aws/credentials) are not set up, or the AWS_PROFILE environment variable is referring to the wrong IAM user?", e);
            else
                throw e;
        }
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
