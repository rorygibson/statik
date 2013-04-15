package statik.route;

import statik.AuthStore;
import statik.Http;
import statik.SessionStore;
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
         if (sessionStore.hasSession(request.cookie(Http.COOKIE_NAME))) {
             LOG.debug(("Log out [" + sessionStore.usernameFor(Http.sessionFrom(request)) + "]"));
             response.removeCookie(Http.COOKIE_NAME);
             response.redirect("/");
             return Http.EMPTY_RESPONSE;
         }
         response.status(401);
         return Http.EMPTY_RESPONSE;
     }
}
