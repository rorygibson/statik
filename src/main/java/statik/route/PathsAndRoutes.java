package statik.route;

public class PathsAndRoutes {

    // Paths
    public static final String ROOT = "/";
    public static final String LOGIN_ERROR = "login-error";
    public static final String LOGIN_ERROR_REDIRECT = "/statik/" + LOGIN_ERROR;
    protected static final String RESOURCE_ROOT_PATH = "statik-resources/";
    static final String ADD_USER_HTML = "add-user";
    static final String USERS_HTML = "users";
    public static final String AUTH = "/statik/auth";
    static final String LOGIN_FORM = "login";
    static final String LOGIN_ALREADY = "login-already";
    static final String EDITOR_HTML = "wysihtml5/editor";
    public static final String RESOURCES = "/statik/resources";

    // Routes
    public static final String ROOT_GLOB_ALL = "/*";
    public static final String CLEAR_DB = "/clear-db";
    public static final String SHUTDOWN = "/shutdown";
    public static final String STATIK_LOGOUT = "/statik/logout";
    public static final String STATIK_LOGIN = "/statik/login";
    public static final String STATIK_LOGIN_ERROR = "/statik/login-error";
    public static final String STATIK_AUTH = "/statik/auth";
    public static final String STATIK_ADMIN_USERS = "/statik/admin/users";
    public static final String STATIK_ADMIN_USER = "/statik/admin/user";
    public static final String STATIK_RESOURCES = "/statik/resources/*";
    public static final String STATIK_CONTENT = "/statik/content";
    public static final String STATIK_EDITOR = "/statik/editor";
}
