package statik.route;


import spark.Request;

public class LoginErrorRoute extends ThymeLeafResourceRoute {

    public LoginErrorRoute(String route) {
        super(route);
    }

    @Override
    protected String resolveTemplateName(Request request) {
        return "login-error";
    }

}
