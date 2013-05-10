package statik.util;

import spark.Request;

public class Http {
    public static final String EMPTY_RESPONSE = "";
    public static final String COOKIE_NAME = "statik";
    public static final String OK_RESPONSE = "OK";

    public static String sessionFrom(Request request) {
        return request.cookie(COOKIE_NAME);
    }
}
