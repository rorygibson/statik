package com.example.web.route;


import com.example.web.AuthStore;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

import java.util.Map;

public class LoginErrorRoute extends AbstractRoute {

    private static final Logger LOG = Logger.getLogger(LoginErrorRoute.class);

    public LoginErrorRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        return writeClasspathFileToResponse(response, "login-error.html");
    }

}
