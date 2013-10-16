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
        String currentDomain = domainFrom(request);
        String referringDomain = referringDomainFrom(request);

        // if asked to login on http://<non-auth-domain>/statik-login,
        // redirect to this path on the auth domain and set return parameter
        if (!currentDomain.equals(this.authDomain)) {
            response.redirect(authenticationRedirectFor(currentDomain));
            return Http.EMPTY_RESPONSE;
        }

        if (hasSession(request) && StringUtils.isNotBlank(referringDomain)) {
            response.redirect("http://" + referringDomain + PathsAndRoutes.COOKIE_CREATION_ROUTE + "?sessionId=" + sessionIdFrom(request));
            return Http.EMPTY_RESPONSE;
        }

        if (hasSession(request)) {
            response.raw().setContentType("text/html");
            return alreadyLoggedInPage();
        }

        Context ctx = new Context();
        if (StringUtils.isNotBlank(referringDomain)) {
            ctx.setVariable("originalDomain", referringDomain);
        }

        response.raw().setContentType("text/html");
        return processWithThymeLeaf(PathsAndRoutes.LOGIN_FORM_VIEWNAME, ctx);
    }

    private String referringDomainFrom(Request request) {
        return request.raw().getParameter(LoginFormRoute.STATIK_ORIGINAL_DOMAIN);
    }

    private String authenticationRedirectFor(String domain) {
        return "http://" + this.authDomain + PathsAndRoutes.STATIK_LOGIN + "?originalDomain=" + domain;
    }

    private boolean hasSession(Request request) {
        return sessionStore.hasSession(sessionIdFrom(request));
    }

    private String sessionIdFrom(Request request) {
        return request.cookie(Http.COOKIE_NAME);
    }

    private String alreadyLoggedInPage() {
        return processWithThymeLeaf(PathsAndRoutes.LOGIN_ALREADY_VIEWNAME);
    }

}
