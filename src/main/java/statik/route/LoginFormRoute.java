package statik.route;

import spark.Request;
import spark.Response;

public class LoginFormRoute extends CESResourceRoute {

    private static final String LOGIN_FORM_HTML = "login.html";

    public LoginFormRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        return writeClasspathFileToResponse(response, LOGIN_FORM_HTML);
    }
}
