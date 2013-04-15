package com.example.web;

import spark.Request;

public class Http {
    public static final String EMPTY_RESPONSE = "";
    public static final String COOKIE_NAME = "ces";

    public static String sessionFrom(Request request) {
        return request.cookie(COOKIE_NAME);
    }
}
