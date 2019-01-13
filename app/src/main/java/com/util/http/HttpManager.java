package com.util.http;

public class HttpManager {
    private final static HttpManager INSTANCE = new HttpManager();

    private HttpManager() {
    }

    public static HttpManager $() {
        return INSTANCE;
    }

}
