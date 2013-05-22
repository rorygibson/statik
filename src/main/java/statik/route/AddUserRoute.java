package statik.route;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;
import statik.auth.AuthStore;

public class AddUserRoute extends ThymeLeafResourceRoute {

    private final AuthStore authStore;
    private static final Logger LOG = LoggerFactory.getLogger(AddUserRoute.class);

    public AddUserRoute(String route, AuthStore authStore) {
        super(route);
        this.authStore = authStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        if (request.requestMethod().equalsIgnoreCase("GET")) {
            return doGet();
        }
        return doPost(request);
    }

    private Object doPost(Request request) {
        String username = request.queryParams("username");
        String password = request.queryParams("password");
        String passwordAgain = request.queryParams("password-again");

        LOG.info("Creating user [" + username + "]");

        Context ctx = new Context();
        boolean added = false;
        if (StringUtils.isNotBlank(password) && !StringUtils.equals(password, passwordAgain)) {
            ctx.setVariable("errorMessage", "Passwords must match and must not be blank"); // TODO i18n
            ctx.setVariable("username", username);
        } else {
            this.authStore.addUser(username, password, false);
            added = true;
        }

        ctx.setVariable("addedUser", added);
        ctx.setVariable("addedUsername", username);
        return processWithThymeLeaf(PathsAndRoutes.ADD_USER_VIEWNAME, ctx);
    }

    private Object doGet() {
        return processWithThymeLeaf(PathsAndRoutes.ADD_USER_VIEWNAME);
    }

}
