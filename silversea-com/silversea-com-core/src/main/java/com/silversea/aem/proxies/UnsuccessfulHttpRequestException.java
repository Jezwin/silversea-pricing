package com.silversea.aem.proxies;

public class UnsuccessfulHttpRequestException extends Exception {
    private int code;
    private String url;

    public UnsuccessfulHttpRequestException(int code, String url) {
        super("Http Request Returned a Status Code of " + code);
        this.code = code;
        this.url = url;
    }

    public int getCode() {
        return code;
    }

    public String getUrl() {
        return url;
    }
}
