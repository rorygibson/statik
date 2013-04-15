package com.example.web.route;

import com.example.web.AuthStore;
import org.apache.log4j.Logger;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractAuthenticatedRoute extends AbstractRoute {

    protected static final String COOKIE_NAME = "ces";
    protected static Map<String, String> sessionStore = new HashMap<String, String>();
    private static final Logger LOG = Logger.getLogger(AbstractAuthenticatedRoute.class);
    private final AuthStore authStore;

    public AbstractAuthenticatedRoute(String route, AuthStore authStore) {
        super(route);
        this.authStore = authStore;
    }

    public boolean hasSession(Request request) {
        if (sessionStore.containsKey(request.cookie(COOKIE_NAME))) {
            LOG.trace("User has a session");
            return true;
        }

        LOG.trace("User does not have a session");
        return false;
    }

    protected boolean auth(String username, String password) {
        if (this.authStore.auth(username, password)) {
            LOG.debug("User is authenticated");
            return true;
        }

        LOG.debug("User is NOT authenticated");
        return false;
    }

    protected String createSessionFor(String username) {
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, username);
        return sessionId;
    }

    protected String usernameForSession(String sessionId) {
        return sessionStore.get(sessionId);
    }

    protected String sessionFrom(Request request) {
        return request.cookie(COOKIE_NAME);
    }

}
