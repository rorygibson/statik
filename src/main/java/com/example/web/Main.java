package com.example.web;

import com.example.web.route.*;
import org.apache.log4j.Logger;
import spark.Spark;

import java.util.Properties;

public class Main implements spark.servlet.SparkApplication {

    private static final Logger LOG = Logger.getLogger(Main.class);

    private static final String CONFIG_FILENAME = "config.properties";
    private static final String USERS_DB_FILENAME = "users.properties";

    private static final String FILE_BASE = "fileBase";
    private static final String WELCOME_FILE = "welcomeFile";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private boolean configured = false;

    private Database database;
    private AuthStore authStore;
    private SessionStore sessionStore;

    private String fileBase;
    private String welcomeFile;
    private String username;
    private String password;

    public static void main(String[] args) {
        new Main().init();
    }

    @Override
    public void init() {
        if (!configured) {
            this.configure(CONFIG_FILENAME);

            this.database = new MongoDatabase();
            this.database.configure(CONFIG_FILENAME);

            this.authStore = new AuthStore();
            this.authStore.configure(USERS_DB_FILENAME);

            this.sessionStore = new SessionStore();
        }


        LOG.info("Setting up routes");
        Spark.get(new LogoutRoute("/logout", this.authStore, this.sessionStore));
        Spark.get(new LoginFormRoute("/login"));
        Spark.get(new LoginErrorRoute("/login-error"));
        Spark.post(new LoginRoute("/auth", this.authStore, this.sessionStore));
        Spark.post(new ContentRoute(this.database, "/content"));
        Spark.get(new CESResourceRoute("/ces-resources/:file"));
        Spark.get(new EditableFileRoute(this.database, this.fileBase, "/", this.welcomeFile, this.authStore, this.sessionStore));
        Spark.get(new EditableFileRoute(this.database, this.fileBase, "/*", this.authStore, this.sessionStore));
    }

    private void configure(String configFilename) {
        LOG.info("Configuring from [" + configFilename + "]");
        Properties config = PropertiesLoader.loadProperties(configFilename);

        this.fileBase = config.getProperty(FILE_BASE);
        this.welcomeFile = config.getProperty(WELCOME_FILE);
        this.username = config.getProperty(USERNAME);
        this.password = config.getProperty(PASSWORD);

        this.configured = true;
        LOG.debug("File base is " + fileBase);
        LOG.debug("Welcome file is " + welcomeFile);
    }
}
