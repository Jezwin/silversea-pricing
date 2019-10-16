package com.silversea.aem.proxies;

import java.io.IOException;

public interface ApiClient {
    String Get(String url) throws IOException, UnsuccessfulHttpRequestException;
}
