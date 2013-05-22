package statik.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;
import spark.Request;
import spark.Response;
import statik.route.PathsAndRoutes;
import statik.session.SessionStore;
import statik.util.Http;

import javax.servlet.http.HttpServletRequest;

public class SecureFilter extends Filter {

    private static final Logger LOG = LoggerFactory.getLogger(SecureFilter.class);
    private final String prefix;
    private final SessionStore sessionStore;

    public SecureFilter(String prefix, SessionStore sessionStore) {
        this.prefix = prefix;
        this.sessionStore = sessionStore;
    }

    @Override
    public void handle(Request request, Response response) {
        String path = pathFrom(request);
        if (!path.startsWith(this.prefix)) {
            return;
        }

        LOG.debug("Secured path [" + path + "]");
        if (isAuthenticated(request)) {
            LOG.debug("Already cookied");
            return;
        }

        response.redirect(PathsAndRoutes.STATIK_LOGIN);
    }

    private boolean isAuthenticated(Request request) {
        return sessionStore.hasSession(Http.sessionFrom(request));
    }

    private String pathFrom(Request request) {
        HttpServletRequest httpReq = request.raw();
        return httpReq.getPathInfo() == null ? httpReq.getServletPath() : httpReq.getPathInfo();
    }
}
