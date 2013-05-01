package statik.route;

import spark.Request;
import spark.Response;
import statik.util.Http;
import statik.session.SessionStore;

public class LoginFormRoute extends ThymeLeafResourceRoute {

    private static final String LOGIN_FORM = "login";
    private static final String LOGIN_ALREADY = "login-already";
    private final SessionStore sessionStore;

    public LoginFormRoute(String route, SessionStore sessionStore) {
        super(route);
        this.sessionStore = sessionStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        if (sessionStore.hasSession(request.cookie(Http.COOKIE_NAME))) {
            return processWithThymeLeaf(LOGIN_ALREADY);
        }
        return processWithThymeLeaf(LOGIN_FORM);
    }

}
