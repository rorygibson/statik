package statik.route;


import spark.Request;
import spark.Response;

public class LoginErrorRoute extends ResourceRoute {

    public LoginErrorRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        return writeClasspathFileToResponse(response, "login-error.html");
    }

}
