package statik.route;

import spark.Request;
import spark.Response;
import spark.Route;
import statik.util.Http;

public class EditRedirectRoute extends Route {

    private final String authDomain;

    public EditRedirectRoute(String route, String authDomain) {
        super(route);
        this.authDomain = authDomain;
    }

    @Override
    public Object handle(Request request, Response response) {
        String uri = "http://" + authDomain + PathsAndRoutes.STATIK_LOGIN + "?originalDomain=" + request.raw().getServerName();
        response.redirect(uri);
        return Http.EMPTY_RESPONSE;
    }
}
