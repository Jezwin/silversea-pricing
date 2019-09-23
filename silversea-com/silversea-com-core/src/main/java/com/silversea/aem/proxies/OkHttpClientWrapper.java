package com.silversea.aem.proxies;

import com.silversea.aem.utils.AwsSecretsManager;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class OkHttpClientWrapper implements ApiClient {

    private String username;
    private String password;
    private OkHttpClient client;

    public OkHttpClientWrapper(AwsSecretsManager secretManager){
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

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
