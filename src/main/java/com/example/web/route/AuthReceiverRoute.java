package com.example.web.route;


import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

import java.util.Map;

public class AuthReceiverRoute extends AbstractAuthenticatedRoute {

    private static final Logger LOG = Logger.getLogger(AuthReceiverRoute.class);

    public AuthReceiverRoute(String route, String username, String password) {
        super(route, username, password);
    }

    @Override
    public Object handle(Request request, Response response) {
        Map<String, String[]> parameterMap = request.raw().getParameterMap();
        String username = parameterMap.get("username")[0];
        String password = parameterMap.get("password")[0];

        LOG.debug("POST to /auth for user [" + username + "]");

        if (auth(username, password)) {
            String sessionId = sessionId();
            sessionStore.put(sessionId, username);
            response.cookie(COOKIE_NAME, sessionId);
            LOG.debug("Authenticated");
            response.redirect("/");
            return "";
        }

        LOG.debug("Not authenticated");
        response.status(401);
        return "NOAUTH";
    }

}
