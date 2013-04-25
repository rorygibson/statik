package statik.route;

import spark.Request;
import spark.Response;
import statik.Http;
import statik.SessionStore;

public class LoginFormRoute extends CESResourceRoute {

    private static final String LOGIN_FORM_HTML = "login.html";
    private static final String LOGIN_ALREADY_HTML = "login-already.html";
    private final SessionStore sessionStore;

    public LoginFormRoute(String route, SessionStore sessionStore) {
        super(route);
        this.sessionStore = sessionStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        if (sessionStore.hasSession(request.cookie(Http.COOKIE_NAME))) {
            return writeClasspathFileToResponse(response, LOGIN_ALREADY_HTML);
        }
        return writeClasspathFileToResponse(response, LOGIN_FORM_HTML);
    }
}
