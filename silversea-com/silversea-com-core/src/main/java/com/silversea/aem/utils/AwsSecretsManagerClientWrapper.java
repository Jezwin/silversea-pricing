package com.silversea.aem.utils;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.AWSSecretsManagerException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import io.vavr.control.Try;
import org.json.JSONObject;

public class AwsSecretsManagerClientWrapper implements AwsSecretsManager {
    private String region;
    private String secretName;

    public AwsSecretsManagerClientWrapper(String region, String secretName)
    {
        this.region = region;
        this.secretName = secretName;
    }

    @Override
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
                            "If the secret exists, this probably means you're authenticated as an IAM user under a different AWS subscription. " +
                            "The IAM user is picked up from your local AWS CLI settings (~/.aws/credentials and the AWS_PROFILE environment variable).", e);
        } catch (AWSSecretsManagerException e) {
            if (e.getErrorCode().equalsIgnoreCase("AccessDeniedException"))
                throw new Exception(
                        "AWS SecretsManager returned AccessDenied for: " + secretName + ".\n" +
                                "This probably means you're either not authenticated with AWS, or are set up as an IAM user that doesn't have access to the secret. " +
                                "The IAM user is picked up from your local AWS CLI settings (~/.aws/credentials and the AWS_PROFILE environment variable).", e);
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