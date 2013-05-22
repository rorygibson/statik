package statik.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;
import statik.auth.AuthStore;

public class DeleteUserRoute extends ThymeLeafResourceRoute {

    private final AuthStore authStore;
    private static final Logger LOG = LoggerFactory.getLogger(DeleteUserRoute.class);

    public DeleteUserRoute(String route, AuthStore authStore) {
        super(route);
        this.authStore = authStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        String username = request.queryParams("username");
        LOG.info("Deleting user [" + username + "]");

        this.authStore.removeUser(username);
        Context ctx = new Context();
        ctx.setVariable("users", this.authStore.users());
        ctx.setVariable("deletedUser", true);

        return processWithThymeLeaf(PathsAndRoutes.USERS_VIEWNAME, ctx);
    }

}
