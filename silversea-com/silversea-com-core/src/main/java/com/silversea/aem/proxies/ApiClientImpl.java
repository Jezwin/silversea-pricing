package com.silversea.aem.proxies;

import com.silversea.aem.utils.AwsSecretsManager;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;

public class ApiClientImpl implements ApiClient {

    private String username;
    private String password;
    private OkHttpClient client;

    public ApiClientImpl(AwsSecretsManager secretManager){
        username = secretManager.getValue("username").get();
        password =  secretManager.getValue("password").get();
        client = new OkHttpClient();
    }

    @Override
    public String Get(String url) throws IOException {

        String credential = Credentials.basic(username, password);

        Request request = new Request.Builder()
                .header("Authorization", credential)
                .url(url)
                .build();

        return client.newCall(request).execute().toString();
    }
}
