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
             response.removeCookie(COOKIE_NAME);
             LOG.debug(("Cookie removed"));
             response.redirect("/");
             return "";
         }
         response.status(401);
         return "NO SESSION FOUND";
     }
}
