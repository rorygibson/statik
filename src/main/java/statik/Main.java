package statik;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.log4j.Logger;
import spark.Spark;
import statik.auth.AuthStore;
import statik.content.ContentStore;
import statik.content.MongoContentStore;
import statik.route.*;
import statik.session.MongoSessionStore;
import statik.session.SessionStore;

public class Main implements spark.servlet.SparkApplication {

    private static final Logger LOG = Logger.getLogger(Main.class);

    private static final String CONFIG_FILENAME = "config.properties";
    private static final String USERS_DB_FILENAME = "users.properties";

    private static final String FILE_BASE = "fileBase";
    private static final String WELCOME_FILE = "welcomeFile";
    private static final String TEST_MODE = "testMode";

    private boolean configured = false;

    private ContentStore contentStore;
    private AuthStore authStore;
    private SessionStore sessionStore;

    private String fileBase;
    private String welcomeFile;
    private boolean testMode = false;

    public static void main(String[] args) {
        new Main().init();
    }

    @Override
    public void init() {
        if (!configured) {
            this.configure(CONFIG_FILENAME);

            this.contentStore = new MongoContentStore();
            this.contentStore.configure(CONFIG_FILENAME);

            this.authStore = new AuthStore();
            this.authStore.configure(USERS_DB_FILENAME);

            this.sessionStore = new MongoSessionStore();
            this.sessionStore.configure(CONFIG_FILENAME);
        }

        if (testMode) {
            LOG.info("Setting up test-only routes");
            Spark.get(new ClearDbRoute("/clear-db", this.contentStore, this.sessionStore));
            Spark.get(new ShutdownRoute("/shutdown"));
        }

        LOG.info("Setting up routes");
        Spark.get(new LogoutRoute("/logout", this.sessionStore));
        Spark.get(new LoginFormRoute("/login", this.sessionStore));
        Spark.get(new LoginErrorRoute("/login-error"));
        Spark.post(new LoginRoute("/auth", this.authStore, this.sessionStore));
        Spark.post(new ContentRoute(this.contentStore, "/content"));

        Spark.get(new ResourceRoute("/statik-resources/*"));

        Spark.get(new EditorRoute("/statik-editor", this.contentStore));

        Spark.get(new EditableFileRoute(this.contentStore, this.fileBase, "/", this.welcomeFile, this.sessionStore));
        Spark.get(new EditableFileRoute(this.contentStore, this.fileBase, "/*", this.sessionStore));
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

        this.configured = true;
        LOG.debug("Test mode is " + testMode);
        LOG.debug("File base is " + fileBase);
        LOG.debug("Welcome file is " + welcomeFile);
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
