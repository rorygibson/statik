package statik.route;

import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;
import statik.session.SessionStore;
import statik.util.Http;

public class LoginFormRoute extends ThymeLeafResourceRoute {

    public static final String STATIK_ORIGINAL_DOMAIN = "originalDomain";
    private final SessionStore sessionStore;
    private final String authDomain;

    public LoginFormRoute(String route, SessionStore sessionStore, String authDomain) {
        super(route);
        this.sessionStore = sessionStore;
        this.authDomain = authDomain;
    }

    @Override
    public Object handle(Request request, Response response) {
        String serverName = request.raw().getServerName();

        // if asked to login on http://non-auth-domain/statik-login, redirect to self on auth domain and set return header
        if (!serverName.equals(this.authDomain)) {
            response.redirect("http://" + this.authDomain + PathsAndRoutes.STATIK_LOGIN + "?originalDomain=" + serverName);
            return Http.EMPTY_RESPONSE;
        }

        if (sessionStore.hasSession(request.cookie(Http.COOKIE_NAME))) {
            return processWithThymeLeaf(PathsAndRoutes.LOGIN_ALREADY_VIEWNAME);
        }

        Context ctx = new Context();
        String originalDomain = request.raw().getParameter(LoginFormRoute.STATIK_ORIGINAL_DOMAIN);
        if (StringUtils.isNotBlank(originalDomain)) {
            ctx.setVariable("originalDomain", originalDomain);
        }
        return processWithThymeLeaf(PathsAndRoutes.LOGIN_FORM_VIEWNAME, ctx);
    }

}
