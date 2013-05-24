package statik.route;


import org.apache.commons.lang3.StringUtils;
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
    private static final String ORIGINAL_DOMAIN = "originalDomain";
    private static final String SESSION_CREATED = "sessionCreated";
    private final AuthStore authStore;
    private final SessionStore sessionStore;

    public LoginRoute(String route, AuthStore authStore, SessionStore sessionStore, String domain) {
        super(route);
        this.authStore = authStore;
        this.sessionStore = sessionStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        Map<String, String[]> parameterMap = request.raw().getParameterMap();
        String username = parameterMap.get(USERNAME)[0];
        String password = parameterMap.get(PASSWORD)[0];
        String originalDomain = parameterMap.get(ORIGINAL_DOMAIN)[0];

        LOG.debug("POST to " + PathsAndRoutes.STATIK_AUTH + " for user [" + username + "], originalDomain is [" + originalDomain + "]");

        if (authStore.auth(username, password)) {
            String sessionId = sessionStore.createSession(username);
            response.raw().addCookie(createCookie(sessionId));

            if (StringUtils.isNotBlank(originalDomain)) {
                // redirect to originating domain to cookie them up "over there"
                response.redirect(cookieRouteFor(originalDomain, sessionId));
            } else {
                response.redirect(PathsAndRoutes.ROOT);
            }

            return Http.OK_RESPONSE;
        }

        response.redirect(PathsAndRoutes.STATIK_LOGIN_ERROR);
        return Http.EMPTY_RESPONSE;
    }

    private String cookieRouteFor(String originalDomain, String sessionId) {
        return "http://" + originalDomain + PathsAndRoutes.COOKIE_CREATION_ROUTE + "?sessionId=" + sessionId;
    }


    private Cookie createCookie(String sessionId) {
        Cookie cookie = new Cookie(Http.COOKIE_NAME, sessionId);
        cookie.setPath(PathsAndRoutes.ROOT);
        cookie.setMaxAge(Integer.MAX_VALUE);
        return cookie;
    }
}
