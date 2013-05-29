package statik.route;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;
import statik.auth.AuthStore;
import statik.auth.User;
import statik.util.PasswordValidator;

public class AddUserRoute extends ThymeLeafResourceRoute {

    // form and query params
    public static final String USERNAME = "username";
    public static final String ORIGINAL_USERNAME = "originalUsername";
    public static final String PASSWORD = "password";
    public static final String PASSWORD_AGAIN = "password-again";
    public static final String ADDED_USERNAME = "addedUsername";
    public static final String EDIT = "edit";
    public static final String EDIT_MODE = "editMode";
    public static final String GET = "GET";
    public static final String USERNAME_TO_EDIT_PARAM = "u";

    // error messages and flash
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String ERROR = "error";
    public static final String FLASH = "flash";
    public static final String FLASH_MESSAGE = "flashMessage";

    // message bundle keys
    public static final String USER_EDITED_MSG = "user.edited";
    public static final String USER_ADDED_MSG = "user.added";
    public static final String PASSWORDS_MUST_MATCH_MSG = "passwords.must.match";

    // other
    public static final String TRUE = "true";

    private final AuthStore authStore;
    private static final Logger LOG = LoggerFactory.getLogger(AddUserRoute.class);
    private final PasswordValidator passwordValidator = new PasswordValidator();

    public AddUserRoute(String route, AuthStore authStore) {
        super(route);
        this.authStore = authStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        if (isEditRequest(request)) {
            return doGetForEdit(userFor(request));
        }

        if (isGet(request)) {
            return doGet();
        }

        return doPost(request);
    }

    private Object doPost(Request request) {
        String username = request.queryParams(USERNAME);
        String originalUsername = request.queryParams(ORIGINAL_USERNAME);
        String password = request.queryParams(PASSWORD);
        String passwordAgain = request.queryParams(PASSWORD_AGAIN);

        Context ctx;
        if (isUserUpdate(request)) {
            ctx = updateUser(username, originalUsername, password, passwordAgain);
        } else {
            ctx = createUser(username, password, passwordAgain);
        }

        return processWithThymeLeaf(PathsAndRoutes.ADD_USER_VIEWNAME, ctx);
    }

    private Context createUser(String username, String password, String passwordAgain) {
        LOG.info("Creating user [" + username + "]");

        Context ctx = new Context();

        if (!passwordValidator.validPasswords(password, passwordAgain)) {
            ctx.setVariable(ERROR_MESSAGE, PASSWORDS_MUST_MATCH_MSG); // TODO i18n
            ctx.setVariable(ERROR, true);
            ctx.setVariable(USERNAME, username);
        } else {
            ctx.setVariable(FLASH, true);
            ctx.setVariable(FLASH_MESSAGE, USER_ADDED_MSG);
            ctx.setVariable(ADDED_USERNAME, username);
        }

        this.authStore.addUser(username, password, false);
        return ctx;
    }

    private Context updateUser(String username, String originalUsername, String password, String passwordAgain) {
        LOG.info("Editing user with original username [" + originalUsername + "], new username is [" + username + "]");
        User u = this.authStore.user(originalUsername);
        u.setUsername(username);
        if (StringUtils.isNotBlank(password)) {
            if (passwordValidator.validPasswords(password, passwordAgain)) {
                u.setPassword(password);
            }
        }
        this.authStore.updateUser(originalUsername, u);

        Context ctx = new Context();
        ctx.setVariable(FLASH, true); // TODO i18n
        ctx.setVariable(FLASH_MESSAGE, USER_EDITED_MSG);
        return ctx;
    }

    private boolean isUserUpdate(Request request) {
        String edit = request.queryParams(EDIT);
        if (StringUtils.equals(edit, TRUE)) {
            return true;
        }
        return false;
    }

    private boolean validPasswords(String password, String passwordAgain) {
        return passwordValidator.validPasswords(password, passwordAgain);
    }

    private Object doGetForEdit(User user) {
        Context ctx = new Context();
        ctx.setVariable(USERNAME, user.getUsername());
        ctx.setVariable(EDIT_MODE, true);
        return processWithThymeLeaf(PathsAndRoutes.ADD_USER_VIEWNAME, ctx);
    }

    private Object doGet() {
        return processWithThymeLeaf(PathsAndRoutes.ADD_USER_VIEWNAME);
    }

    private boolean isGet(Request request) {
        return request.requestMethod().equalsIgnoreCase(GET);
    }

    private User userFor(Request request) {
        return this.authStore.user(request.queryParams(USERNAME_TO_EDIT_PARAM));
    }

    private boolean isEditRequest(Request request) {
        return isGet(request) && StringUtils.isNotBlank(request.queryParams(USERNAME_TO_EDIT_PARAM));
    }
}
