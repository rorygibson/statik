package statik.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;
import statik.auth.AuthStore;
import statik.auth.User;

import java.util.Collection;

public class UserListRoute extends ThymeLeafResourceRoute {

    private final AuthStore authStore;
    private static final Logger LOG = LoggerFactory.getLogger(UserListRoute.class);

    public UserListRoute(String route, AuthStore authStore) {
        super(route);
        this.authStore = authStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        String templateName = PathsAndRoutes.USERS_VIEWNAME;
        Context ctx = new Context();
        Collection<User> users = this.authStore.users();
        String joined = join(users);
        LOG.info("Listing [" + users.size() + "] users, [" + joined + "]");
        ctx.setVariable("users", users);
        response.raw().setContentType("text/html");
        return processWithThymeLeaf(templateName, ctx);
    }

    private String join(Collection<User> users) {
        StringBuffer buf = new StringBuffer();
        for (User u : users) {
            buf.append(u.getUsername());
            buf.append(", ");
        }
        String joined = buf.toString();
        return joined.substring(0,joined.length() - 2);
    }

}
