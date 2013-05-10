package statik.route;

import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;
import statik.auth.AuthStore;

public class UserListRoute extends ThymeLeafResourceRoute {

    private final AuthStore authStore;

    public UserListRoute(String route, AuthStore authStore) {
        super(route);
        this.authStore = authStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        String templateName = PathsAndRoutes.USERS_HTML;
        Context ctx = new Context();
        ctx.setVariable("users", this.authStore.users());
        return processWithThymeLeaf(templateName, ctx);
    }

}
