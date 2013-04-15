package com.example.web.route;

import com.example.web.AuthStore;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

public class LogoutRoute extends AbstractAuthenticatedRoute {

    private static final Logger LOG = Logger.getLogger(LogoutRoute.class);

    public LogoutRoute(String route, AuthStore authStore) {
        super(route, authStore);
    }

    @Override
     public Object handle(Request request, Response response) {
         if (hasSession(request)) {
             LOG.debug(("Log out [" + usernameForSession(sessionFrom(request)) + "]"));
             response.removeCookie(COOKIE_NAME);
             response.redirect("/");
             return EMPTY_RESPONSE;
         }
         response.status(401);
         return EMPTY_RESPONSE;
     }
}
