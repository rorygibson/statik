package statik.route;


import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

public class LoginErrorRoute extends ResourceRoute {

    private static final Logger LOG = Logger.getLogger(LoginErrorRoute.class);

    public LoginErrorRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        return writeClasspathFileToResponse(response, "login-error.html");
    }

}
