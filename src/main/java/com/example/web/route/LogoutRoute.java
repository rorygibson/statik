package com.example.web.route;

import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

public class LogoutRoute extends AbstractAuthenticatedRoute {

    private static final Logger LOG = Logger.getLogger(LogoutRoute.class);

    public LogoutRoute(String route, String username, String password) {
        super(route, username, password);
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
