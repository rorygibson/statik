package statik.route;

public class PathsAndRoutes {

    // File paths
    public static final String ROOT = "/";
    public static final String RESOURCE_ROOT_PATH = "statik-resources/";

    // View names
    public static final String LOGIN_ERROR_VIEWNAME = "login-error";
    public static final String ADD_USER_VIEWNAME = "add-user";
    public static final String USERS_VIEWNAME = "users";
    public static final String LOGIN_FORM_VIEWNAME = "login";
    public static final String LOGIN_ALREADY_VIEWNAME = "login-already";
    public static final String UPLOAD_DIALOG_VIEWNAME = "upload-list";

    // Routes
    public static final String EDITOR_VIEWNAME = "editor";


    // Secured routes
    public static final String STATIK_CLEAR_DB = "/statik/clear-db";
    public static final String STATIK_SHUTDOWN = "/statik/shutdown";
    public static final String STATIK_LOGOUT = "/statik/logout";
    public static final String STATIK_ADMIN_USERS = "/statik/users";
    public static final String STATIK_ADMIN_USER = "/statik/user";
    public static final String STATIK_CONTENT = "/statik/content";
    public static final String STATIK_EDITOR = "/statik/editor";
    public static final String MAKE_CONTENT_LIVE = "/statik/make-it-so";
    public static final String STATIK_DELETE_USER = "/statik/delete-user";
    public static final String UPLOAD_ROUTE = "/statik/upload";

    // Unsecured routes
    public static final String ROOT_GLOB_ALL = "/*";
    public static final String STATIK_LOGIN = "/statik-login";
    public static final String STATIK_LOGIN_ERROR = "/statik-login-error";
    public static final String STATIK_AUTH = "/statik-auth";
    public static final String STATIK_RESOURCES = "/statik-resources";
    public static final String STATIK_RESOURCES_GLOB = "/statik-resources/*";
    public static final String COOKIE_CREATION_ROUTE = "/statik-cookie";
    public static final String EDIT_ROUTE = "/edit";
    public static final String CONTROL_BOX = "/statik/control-box";
    public static final String UPLOAD_LIST_ROUTE = "/statik/upload-list";
    public static final String UPLOADED_FILES_GLOB = "/statik-uploads/*";
    public static final String UPLOADED_FILES_PREFIX = "/statik-uploads/";
    public static final String STATIK_UPLOAD_LIST_DIALOG = "/statik/upload-list-dialog";
}
