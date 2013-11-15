package statik;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import statik.auth.AuthStore;
import statik.auth.RDBMSAuthStore;
import statik.auth.SecureFilter;
import statik.auth.User;
import statik.content.ContentStore;
import statik.content.LanguageFilter;import statik.content.RDBMSContentStore;
import statik.route.*;
import statik.session.RDBMSSessionStore;
import statik.session.SessionStore;

public class Main implements spark.servlet.SparkApplication {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final String CONFIG_FILENAME = "config.properties";

    private static final String FILE_BASE = "fileBase";
    private static final String WELCOME_FILE = "welcomeFile";
    private static final String TEST_MODE = "testMode";
    private static final String NOT_FOUND_PAGE = "404page";
    private static final String PORT = "port";
    private static final int DEFAULT_PORT = 4567;
    private static final String AUTH_DOMAIN = "authDomain";
    private static final String UPLOAD_LOCATION = "uploadStorageLocation";
    private static final String NOTIFIER_URL = "notifierUrl";

    private static String configFile;

    private String uploadDir;
    private ContentStore contentStore;
    private AuthStore authStore;
    private SessionStore sessionStore;

    private String fileBase;
    private String welcomeFile;
    private boolean testMode = false;
    private String notFoundPage;
    private static int port;
    private String authDomain;
    private String notifierUrl;

    public static void main(String[] args) {
        Main main = new Main();
        configFile = System.getProperty("config.filename", CONFIG_FILENAME);
        main.configure(configFile);
        Spark.setPort(port);

        main.populate();
        main.addTestOnlyRoutes();
        main.addStatikRoutes();
    }

    @Override
    public void init() {
        configFile = System.getProperty("config.filename", CONFIG_FILENAME);
        configure(configFile);

        populate();
        addTestOnlyRoutes();
        addStatikRoutes();
    }

    private void populate() {
        this.contentStore = new RDBMSContentStore();
        this.contentStore.configure(configFile);

        this.authStore = new RDBMSAuthStore();
        this.authStore.configure(configFile);

        if (this.authStore.users().isEmpty()) {
            addDefaultUser();
        }

        this.sessionStore = new RDBMSSessionStore();
        this.sessionStore.configure(configFile);
    }

    private void addDefaultUser() {
        LOG.error("Adding default user [admin]");
        User defaultUser = new User("admin", "password", true);
        this.authStore.addUser(defaultUser);
    }

    private void addStatikRoutes() {

        Spark.before(new LanguageFilter("/*", new Notifier(this.notifierUrl)));
        Spark.before(new SecureFilter("/statik/", this.sessionStore));

        LOG.info("Setting up statik routes");
        Spark.get(new LogoutRoute(PathsAndRoutes.STATIK_LOGOUT, this.sessionStore));
        Spark.get(new LoginFormRoute(PathsAndRoutes.STATIK_LOGIN, this.sessionStore, this.authDomain));
        Spark.get(new LoginErrorRoute(PathsAndRoutes.STATIK_LOGIN_ERROR));
        Spark.post(new LoginRoute(PathsAndRoutes.STATIK_AUTH, this.authStore, this.sessionStore, this.authDomain));
        Spark.get(new CookieRoute(PathsAndRoutes.COOKIE_CREATION_ROUTE, this.sessionStore));
        Spark.get(new EditRedirectRoute(PathsAndRoutes .EDIT_ROUTE, this.authDomain));

        Spark.get(new UserListRoute(PathsAndRoutes.STATIK_ADMIN_USERS, this.authStore));
        Spark.get(new AddUserRoute(PathsAndRoutes.STATIK_ADMIN_USER, this.authStore));
        Spark.post(new AddUserRoute(PathsAndRoutes.STATIK_ADMIN_USER, this.authStore));
        Spark.get(new DeleteUserRoute(PathsAndRoutes.STATIK_DELETE_USER, this.authStore));

        Spark.post(new UploadRoute(PathsAndRoutes.UPLOAD_ROUTE, this.uploadDir));
        Spark.get(new UploadListRoute(PathsAndRoutes.UPLOAD_LIST_ROUTE, this.uploadDir));
        Spark.get(new UploadedFilesRoute(PathsAndRoutes.UPLOADED_FILES_GLOB, this.uploadDir));
        Spark.get(new UploadListDialogRoute(PathsAndRoutes.STATIK_UPLOAD_LIST_DIALOG));

        Spark.get(new ControlBoxRoute(PathsAndRoutes.CONTROL_BOX, this.fileBase));
        Spark.get(new ResourceRoute(PathsAndRoutes.STATIK_RESOURCES_GLOB));
        Spark.post(new ContentRoute(PathsAndRoutes.STATIK_CONTENT, this.contentStore));
        Spark.get(new EditorRoute(PathsAndRoutes.STATIK_EDITOR, this.contentStore));
        Spark.post(new MakeContentLiveRoute(PathsAndRoutes.MAKE_CONTENT_LIVE, this.contentStore));
        Spark.post(new CopyPageRoute(PathsAndRoutes.COPY_PAGE, this.fileBase));

        LOG.info("Setting up editable site routes");
        Spark.get(new EditableFileRoute(this.contentStore, this.fileBase, PathsAndRoutes.UPLOADED_FILES_PREFIX, PathsAndRoutes.ROOT, this.welcomeFile, this.sessionStore, this.notFoundPage));
        Spark.get(new EditableFileRoute(this.contentStore, this.fileBase, PathsAndRoutes.UPLOADED_FILES_PREFIX, PathsAndRoutes.ROOT_GLOB_ALL, this.sessionStore, this.notFoundPage));
    }

    private void addTestOnlyRoutes() {
        if (testMode) {
            LOG.info("Setting up test-only routes");
            Spark.get(new ClearDbRoute(PathsAndRoutes.STATIK_CLEAR_DB, this.contentStore, this.sessionStore, this.authStore));
            Spark.get(new ShutdownRoute(PathsAndRoutes.STATIK_SHUTDOWN));
        }
    }

    private void configure(String configFilename) {
        LOG.info("Configuring from [" + configFilename + "]");

        CompositeConfiguration config = new CompositeConfiguration();
        PropertiesConfiguration propertiesConfiguration = loadPropertiesConfigFrom(configFilename);
        SystemConfiguration systemConfiguration = new SystemConfiguration();
        PropertiesConfiguration defaults = new PropertiesConfiguration();

        defaults.setProperty("testMode", false);

        config.addConfiguration(systemConfiguration);
        config.addConfiguration(propertiesConfiguration);
        config.addConfiguration(defaults);

        this.testMode = config.getBoolean(TEST_MODE);
        this.fileBase = config.getString(FILE_BASE);
        this.welcomeFile = config.getString(WELCOME_FILE);
        this.notFoundPage = config.getString(NOT_FOUND_PAGE);
        this.port = Integer.valueOf(StringUtils.defaultIfEmpty(config.getString(PORT), "" + DEFAULT_PORT));
        this.authDomain = config.getString(AUTH_DOMAIN, "http://localhost:" + this.port);
        this.uploadDir = config.getString(UPLOAD_LOCATION, "/tmp");
        this.notifierUrl = config.getString(NOTIFIER_URL);

        LOG.debug("Test mode is " + testMode);
        LOG.debug("File base is " + fileBase);
        LOG.debug("Welcome file is " + welcomeFile);
        LOG.debug("404 file is " + notFoundPage);
        LOG.debug("Port is " + port);
    }

    private PropertiesConfiguration loadPropertiesConfigFrom(String configFilename) {
        PropertiesConfiguration fileConfig;
        try {
            fileConfig = new PropertiesConfiguration(configFilename);
        } catch (ConfigurationException e) {
            throw new RuntimeException("Couldn't load configuration from " + configFilename);
        }
        return fileConfig;
    }
}
