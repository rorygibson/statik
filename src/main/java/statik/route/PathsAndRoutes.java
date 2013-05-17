package statik.route;

public class PathsAndRoutes {

    // Paths
    public static final String ROOT = "/";
    public static final String RESOURCE_ROOT_PATH = "statik-resources/";
    public static final String AUTH = "/statik/auth";

    // View names
    public static final String LOGIN_ERROR_VIEWNAME = "login-error";
    public static final String ADD_USER_VIEWNAME = "add-user";
    public static final String USERS_VIEWNAME = "users";
    public static final String LOGIN_FORM_VIEWNAME = "login";
    public static final String LOGIN_ALREADY_VIEWNAME = "login-already";

    // Routes
    public static final String EDITOR_VIEWNAME = "wysihtml5/editor";

    // Redirects
    public static final String ROOT_GLOB_ALL = "/*";
    public static final String STATIK_CLEAR_DB = "/statik/clear-db";
    public static final String STATIK_SHUTDOWN = "/statik/shutdown";
    public static final String STATIK_LOGOUT = "/statik/logout";
    public static final String STATIK_LOGIN = "/statik/login";
    public static final String STATIK_LOGIN_ERROR = "/statik/login-error";
    public static final String STATIK_AUTH = "/statik/auth";
    public static final String STATIK_ADMIN_USERS = "/statik/admin/users";
    public static final String STATIK_ADMIN_USER = "/statik/admin/user";
    public static final String STATIK_RESOURCES = "/statik/resources";
    public static final String STATIK_CONTENT = "/statik/content";
    public static final String STATIK_EDITOR = "/statik/editor";
    public static final String STATIK_ADMIN = "/statik/admin/";
    public static final String STATIK_RESOURCES_GLOB = "/statik/resources/*";
    public static final String MAKE_CONTENT_LIVE = "/statik/make-it-so";
}
