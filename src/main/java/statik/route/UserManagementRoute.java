package statik.route;

import spark.Request;
import spark.Response;
import statik.auth.AuthStore;

public class UserManagementRoute extends InternationalisedResourceRoute {

    private static final String USERS_HTML = "users.html";
    private final AuthStore authStore;

    public UserManagementRoute(String route, AuthStore authStore) {
        super(route);
        this.authStore = authStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        // TODO something useful
        // TODO auth
        return i18n(RESOURCE_ROOT_PATH + "/" + USERS_HTML);
    }

}
