package statik.route;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import statik.auth.AuthStore;
import statik.session.SessionStore;
import statik.util.Http;

import javax.servlet.http.Cookie;
import java.util.Map;

public class LoginRoute extends Route {

    private static final Logger LOG = LoggerFactory.getLogger(LoginRoute.class);
    public static final String PASSWORD = "password";
    public static final String USERNAME = "username";
    private final AuthStore authStore;
    private final SessionStore sessionStore;

    public LoginRoute(String route, AuthStore authStore, SessionStore sessionStore) {
        super(route);
        this.authStore = authStore;
        this.sessionStore = sessionStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        Map<String, String[]> parameterMap = request.raw().getParameterMap();
        String username = parameterMap.get(USERNAME)[0];
        String password = parameterMap.get(PASSWORD)[0];

        LOG.debug("POST to /statik-auth for user [" + username + "]");

        if (authStore.auth(username, password)) {
            String sessionId = sessionStore.createSession(username);
            Cookie cookie = new Cookie(Http.COOKIE_NAME, sessionId);
            cookie.setPath("/");
            cookie.setMaxAge(Integer.MAX_VALUE);
            response.raw().addCookie(cookie);
            response.redirect("/");
            return "OK";
        }

        response.redirect("/statik-login-error");
        return Http.EMPTY_RESPONSE;
    }
}
