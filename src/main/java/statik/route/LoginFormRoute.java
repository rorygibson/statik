package statik.route;

import spark.Request;
import spark.Response;
import statik.util.Http;
import statik.session.SessionStore;

public class LoginFormRoute extends ThymeLeafResourceRoute {

    private final SessionStore sessionStore;

    public LoginFormRoute(String route, SessionStore sessionStore) {
        super(route);
        this.sessionStore = sessionStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        if (sessionStore.hasSession(request.cookie(Http.COOKIE_NAME))) {
            return processWithThymeLeaf(PathsAndRoutes.LOGIN_ALREADY);
        }
        return processWithThymeLeaf(PathsAndRoutes.LOGIN_FORM);
    }

}
