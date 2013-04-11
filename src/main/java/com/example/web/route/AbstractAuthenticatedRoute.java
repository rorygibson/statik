package com.example.web.route;

import org.apache.log4j.Logger;
import spark.Request;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAuthenticatedRoute extends AbstractRoute {

    protected static final String COOKIE_NAME = "cms";
    protected static Map<String, String> sessionStore = new HashMap<String, String>();
    private static final Logger LOG = Logger.getLogger(AbstractAuthenticatedRoute.class);
    private final String username;
    private final String password;

    public AbstractAuthenticatedRoute(String route, String username, String password) {
        super(route);
        this.username = username;
        this.password = password;
    }

    public boolean hasSession(Request request) {
        boolean authenticated = false;
        if (sessionStore.containsKey(request.cookie(COOKIE_NAME))) {
            authenticated = true;
            LOG.debug("User is authenticated");
        } else {
            LOG.debug("User is not authenticated");
        }
        return authenticated;
    }

    protected String sessionId() {
        return String.valueOf(Math.random());
    }

    protected boolean auth(String username, String password) {
        return username != null && password != null && username.equals(this.username) && password.equals(this.password);
    }

}
