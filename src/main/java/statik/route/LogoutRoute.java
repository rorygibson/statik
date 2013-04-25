package statik.route;

import statik.auth.AuthStore;
import statik.util.Http;
import statik.session.SessionStore;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutRoute extends Route {

    private static final Logger LOG = Logger.getLogger(LogoutRoute.class);
    private final AuthStore authStore;
    private final SessionStore sessionStore;

    public LogoutRoute(String route, AuthStore authStore, SessionStore sessionStore) {
        super(route);
        this.authStore = authStore;
        this.sessionStore = sessionStore;
    }

    @Override
     public Object handle(Request request, Response response) {
        String cookie = request.cookie(Http.COOKIE_NAME);
        if (sessionStore.hasSession(cookie)) {
             LOG.debug(("Log out [" + sessionStore.usernameFor(Http.sessionFrom(request)) + "]"));
             response.removeCookie(Http.COOKIE_NAME);
             sessionStore.deleteSession(cookie);
             response.redirect("/");
             return Http.EMPTY_RESPONSE;
         }
         response.status(401);
         return Http.EMPTY_RESPONSE;
     }
}
