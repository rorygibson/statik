package com.example.web;

import com.example.web.route.*;
import org.apache.log4j.Logger;
import spark.Spark;

import java.util.Properties;

public class Main implements spark.servlet.SparkApplication {

    private static final Logger LOG = Logger.getLogger(Main.class);
    private static final String MESSAGE_BUNDLE_NAME = "messages.properties";
    private static final String CONFIG_FILENAME = "config.properties";
    private static final String FILE_BASE = "fileBase";
    private static final String WELCOME_FILE = "welcomeFile";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private String fileBase;
    private Database database;
    private boolean configured = false;
    private String welcomeFile;
    private String password;
    private String username;
    private AuthStore authStore;

    public static void main(String[] args) {
        new Main().init();
    }

    @Override
    public void init() {
        if (!configured) {
            this.configure(CONFIG_FILENAME);
        }

        this.database = new MongoDatabase();
        this.database.configure(CONFIG_FILENAME);

        if (database.isEmpty()) {
            loadMessagesIntoDatabaseFrom(MESSAGE_BUNDLE_NAME);
        }

        this.authStore = new AuthStore();
        this.authStore.addUser(this.username, this.password);

        LOG.info("Setting up routes");
        Spark.get(new LogoutRoute("/logout", this.authStore));
        Spark.get(new LoginFormRoute("/login"));
        Spark.get(new LoginErrorRoute("/login-error"));
        Spark.post(new LoginRoute("/auth", this.authStore));
        Spark.post(new ContentRoute(this.database, "/content/:id"));
        Spark.get(new CESResourceRoute("/ces-resources/:file"));
        Spark.get(new EditableFileRoute(this.database, this.fileBase, "/", this.welcomeFile, this.authStore));
        Spark.get(new EditableFileRoute(this.database, this.fileBase, "/*", this.authStore));
    }


    private void configure(String configFilename) {
        LOG.info("Configuring from [" + configFilename + "]");
        Properties config = PropertiesLoader.loadProperties(configFilename);
        this.fileBase = config.getProperty(FILE_BASE);
        this.welcomeFile = config.getProperty(WELCOME_FILE);
        this.configured = true;
        this.username = config.getProperty(USERNAME);
        this.password = config.getProperty(PASSWORD);

        LOG.debug("File base is " + fileBase);
        LOG.debug("Welcome file is " + welcomeFile);
    }


    private void loadMessagesIntoDatabaseFrom(String messageBundleName) {
        LOG.info("Loading messages into DB from [" + messageBundleName + "]");
        Properties content = new PropertiesLoader().loadProperties(messageBundleName);

        for (Object key : content.keySet()) {
            String value = content.getProperty(key.toString());
            this.database.insertOrUpdate(key.toString(), value);
            LOG.debug("Inserted item to database");
        }
    }
}
