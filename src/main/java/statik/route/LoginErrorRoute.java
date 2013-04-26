package statik.route;


import spark.Request;
import spark.Response;

public class LoginErrorRoute extends InternationalisedResourceRoute {

    public LoginErrorRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        return i18n(RESOURCE_ROOT_PATH + "/" + "login-error.html");
    }

}
