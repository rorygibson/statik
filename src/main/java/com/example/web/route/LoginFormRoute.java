package com.example.web.route;

import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

public class LoginFormRoute extends AbstractRoute {

    private static final Logger LOG = Logger.getLogger(LoginFormRoute.class);
    private static final String LOGIN_FORM_HTML = "login.html";

    public LoginFormRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        LOG.debug("serving login form");
        return writeClasspathFileToResponse(response, LOGIN_FORM_HTML);
    }
}
