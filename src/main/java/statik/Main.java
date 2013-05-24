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
import statik.auth.MongoAuthStore;
import statik.auth.SecureFilter;
import statik.auth.User;
import statik.content.ContentStore;
import statik.content.MongoContentStore;
import statik.route.*;
import statik.session.MongoSessionStore;
import statik.session.SessionStore;

public class Main implements spark.servlet.SparkApplication {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final String CONFIG_FILENAME = "config.properties";
    private static final String USERS_DB_FILENAME = "users.properties";

    private static final String FILE_BASE = "fileBase";
    private static final String WELCOME_FILE = "welcomeFile";
    private static final String TEST_MODE = "testMode";
    private static final String NOT_FOUND_PAGE = "404page";
    private static final String PORT = "port";
    private static final int DEFAULT_PORT = 4567;
    private static final String AUTH_DOMAIN = "authDomain";

    private boolean configured = false;

    private ContentStore contentStore;
    private AuthStore authStore;
    private SessionStore sessionStore;

    private String fileBase;
    private String welcomeFile;
    private boolean testMode = false;
    private String notFoundPage;
    private static int port;
    private String authDomain;

    public static void main(String[] args) {
        Main main = new Main();
        main.configure(CONFIG_FILENAME);
        Spark.setPort(port);

        main.populate();
        main.addTestOnlyRoutes();
        main.addStatikRoutes();
    }

    @Override
    public void init() {
        configure(CONFIG_FILENAME);
        populate();
        addTestOnlyRoutes();
        addStatikRoutes();
    }

    private void populate() {
        this.contentStore = new MongoContentStore();
        this.contentStore.configure(CONFIG_FILENAME);

        this.authStore = new MongoAuthStore();
        this.authStore.configure(CONFIG_FILENAME);

        if (this.authStore.users().isEmpty()) {
            addDefaultUser();
        }

        this.sessionStore = new MongoSessionStore();
        this.sessionStore.configure(CONFIG_FILENAME);
    }

    private void addDefaultUser() {
        LOG.error("Adding default user [admin]");
        User defaultUser = new User("admin", "password", true);
        this.authStore.addUser(defaultUser);
    }

    private void addStatikRoutes() {
        Spark.before(new SecureFilter("/statik/", this.sessionStore));

        LOG.info("Setting up statik routes");
        Spark.get(new LogoutRoute(PathsAndRoutes.STATIK_LOGOUT, this.sessionStore));
        Spark.get(new LoginFormRoute(PathsAndRoutes.STATIK_LOGIN, this.sessionStore, this.authDomain));
        Spark.get(new LoginErrorRoute(PathsAndRoutes.STATIK_LOGIN_ERROR));
        Spark.post(new LoginRoute(PathsAndRoutes.STATIK_AUTH, this.authStore, this.sessionStore, this.authDomain));
        Spark.get(new CookieRoute(PathsAndRoutes.COOKIE_CREATION_ROUTE, this.sessionStore));

        Spark.get(new UserListRoute(PathsAndRoutes.STATIK_ADMIN_USERS, this.authStore));
        Spark.get(new AddUserRoute(PathsAndRoutes.STATIK_ADMIN_USER, this.authStore));
        Spark.post(new AddUserRoute(PathsAndRoutes.STATIK_ADMIN_USER, this.authStore));
        Spark.get(new DeleteUserRoute(PathsAndRoutes.STATIK_DELETE_USER, this.authStore));

        Spark.get(new ResourceRoute(PathsAndRoutes.STATIK_RESOURCES_GLOB));
        Spark.post(new ContentRoute(PathsAndRoutes.STATIK_CONTENT, this.contentStore));
        Spark.get(new EditorRoute(PathsAndRoutes.STATIK_EDITOR, this.contentStore));
        Spark.post(new MakeContentLiveRoute(PathsAndRoutes.MAKE_CONTENT_LIVE, this.contentStore));

        LOG.info("Setting up editable site routes");
        Spark.get(new EditableFileRoute(this.contentStore, this.fileBase, PathsAndRoutes.ROOT, this.welcomeFile, this.sessionStore, this.notFoundPage));
        Spark.get(new EditableFileRoute(this.contentStore, this.fileBase, PathsAndRoutes.ROOT_GLOB_ALL, this.sessionStore, this.notFoundPage));
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

        this.configured = true;
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
