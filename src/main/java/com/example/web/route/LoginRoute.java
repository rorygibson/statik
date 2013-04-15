package com.example.web.route;


import com.example.web.AuthStore;
import com.example.web.Http;
import com.example.web.SessionStore;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class LoginRoute extends Route {

    private static final Logger LOG = Logger.getLogger(LoginRoute.class);
    public static final String PASSWORD = "password";
    public static final String USERNAME = "username";
    private final AuthStore authStore;
    private final SessionStore sessionStore;

    public LoginRoute(String route, AuthStore authStore, SessionStore sessionStore) {
        super(route);
        this.authStore = authStore;
        this.sessionStore = sessionStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        Map<String, String[]> parameterMap = request.raw().getParameterMap();
        String username = parameterMap.get(USERNAME)[0];
        String password = parameterMap.get(PASSWORD)[0];

        LOG.debug("POST to /auth for user [" + username + "]");

        if (authStore.auth(username, password)) {
            String sessionId = sessionStore.createSession(username);
            response.cookie(Http.COOKIE_NAME, sessionId);
            response.redirect("/");
            return "OK";
        }

        response.redirect("/login-error");
        return Http.EMPTY_RESPONSE;
    }
}
