package com.example.web.route;


import com.example.web.AuthStore;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

import java.util.Map;

public class LoginRoute extends AbstractAuthenticatedRoute {

    private static final Logger LOG = Logger.getLogger(LoginRoute.class);

    public LoginRoute(String route, AuthStore authStore) {
        super(route, authStore);
    }

    @Override
    public Object handle(Request request, Response response) {
        Map<String, String[]> parameterMap = request.raw().getParameterMap();
        String username = parameterMap.get("username")[0];
        String password = parameterMap.get("password")[0];

        LOG.debug("POST to /auth for user [" + username + "]");

        if (auth(username, password)) {
            String sessionId = createSessionFor(username);
            response.cookie(COOKIE_NAME, sessionId);
            response.redirect("/");
            return "";
        }

        LOG.debug("Not authenticated");
        response.status(401);
        return "NOAUTH";
    }

}
