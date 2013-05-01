package statik.route;

import spark.Request;
import statik.auth.AuthStore;

public class UserManagementRoute extends ThymeLeafResourceRoute {

    private static final String USERS_HTML = "users";
    private final AuthStore authStore;

    public UserManagementRoute(String route, AuthStore authStore) {
        super(route);
        this.authStore = authStore;
    }

    @Override
    protected String resolveTemplateName(Request request) {
        return USERS_HTML;
    }

}
