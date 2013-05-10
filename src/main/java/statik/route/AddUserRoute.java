package statik.route;

import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;
import statik.auth.AuthStore;

public class AddUserRoute extends ThymeLeafResourceRoute {

    private final AuthStore authStore;

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

        Context ctx = new Context();
        boolean added = false;
        if (StringUtils.isNotBlank(password) && !StringUtils.equals(password, passwordAgain)) {
            ctx.setVariable("errorMessage", "Passwords must match and must not be blank"); // TODO i18n
            ctx.setVariable("username", username);
        } else {
            this.authStore.addUser(username, password);
            added = true;
        }

        ctx.setVariable("addedUser", added);
        ctx.setVariable("addedUsername", username);
        return processWithThymeLeaf(PathsAndRoutes.ADD_USER_HTML, ctx);
    }

    private Object doGet() {
        return processWithThymeLeaf(PathsAndRoutes.ADD_USER_HTML);
    }

}
