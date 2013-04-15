package com.example.web.route;

import com.example.web.AuthStore;
import com.example.web.SessionStore;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

public class LogoutRoute extends AbstractRoute {

    private static final Logger LOG = Logger.getLogger(LogoutRoute.class);
    private final AuthStore authStore;
    private final SessionStore sessionStore;

    public LogoutRoute(String route, AuthStore authStore, SessionStore sessionStore) {
        super(route);
        this.authStore = authStore;
        this.sessionStore = sessionStore;
    }

    @Override
     public Object handle(Request request, Response response) {
         if (sessionStore.hasSession(request.cookie(COOKIE_NAME))) {
             LOG.debug(("Log out [" + sessionStore.usernameFor(sessionFrom(request)) + "]"));
             response.removeCookie(COOKIE_NAME);
             response.redirect("/");
             return EMPTY_RESPONSE;
         }
         response.status(401);
         return EMPTY_RESPONSE;
     }
}
