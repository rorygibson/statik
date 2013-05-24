package statik.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import statik.session.SessionStore;
import statik.util.Http;

import javax.servlet.http.Cookie;

public class CookieRoute extends Route {

    private static final Logger LOG = LoggerFactory.getLogger(CookieRoute.class);
    private final SessionStore sessionStore;

    public CookieRoute(String route, SessionStore ss) {
        super(route);
        this.sessionStore = ss;
    }

    @Override
    public Object handle(Request request, Response response) {
        String sessionId = request.raw().getParameter("sessionId");
        boolean validSession = sessionStore.hasSession(sessionId);

        if (!validSession) {
            LOG.warn("Invalid sessionId supplied; malicious attempt suspect [" + sessionId + "]");
            response.status(Http.FORBIDDEN);
            return Http.EMPTY_RESPONSE;
        }

        LOG.debug("Cookieing up user for " + request.raw().getServerName());
        response.raw().addCookie(createCookie(sessionId));
        response.redirect("/");
        return Http.EMPTY_RESPONSE;
    }

    private Cookie createCookie(String sessionId) {
        Cookie cookie = new Cookie(Http.COOKIE_NAME, sessionId);
        cookie.setPath(PathsAndRoutes.ROOT);
        cookie.setMaxAge(Integer.MAX_VALUE);
        return cookie;
    }
}
