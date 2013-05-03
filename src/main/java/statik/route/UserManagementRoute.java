package statik.route;

import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;
import statik.auth.AuthStore;

public class UserManagementRoute extends ThymeLeafResourceRoute {

    private static final String USERS_HTML = "users";
    private final AuthStore authStore;

    public UserManagementRoute(String route, AuthStore authStore) {
        super(route);
        this.authStore = authStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        String templateName = USERS_HTML;
        Context ctx = new Context();
        ctx.setVariable("users", this.authStore.users());
        return processWithThymeLeaf(templateName, ctx);
    }



}
