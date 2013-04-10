package com.example.web;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;

public class Database {
    private static final Logger LOG = Logger.getLogger(Database.class);

    private MongoClient mongoClient = null;

    private DBCollection items = null;
    private DB db = null;

    public String DB_NAME = "";
    private String MONGO_HOST = "";
    private int MONGO_PORT = 0;
    private String MONGO_USERNAME = "";
    private String MONGO_PASSWORD = "";

    private boolean configured = false;

    private static final String CONFIG_FILENAME = "config.properties";


    public boolean isEmpty() {
        int count = items.find().count();
        LOG.info("Number of content items in DB is " + count);
        return count == 0;
    }


    public String get(String id) {
        BasicDBObject dbObject = new BasicDBObject("contentId", id);
        String content = items.findOne(dbObject).get("content").toString();
        return content;
    }

    public void insert(String id, String content) {
        LOG.debug("Updating contentItem [" + id + "] with content, length [" + content.length() + "]");

        BasicDBObject queryObject = new BasicDBObject("contentId", id);
        BasicDBObject updateObject = new BasicDBObject("contentId", id).append("content", content);
        items.update(queryObject, updateObject);

        LOG.debug("Updated in DB");
    }


    public void configure() {
        if (!configured) {
            loadConfig();
            setupDB();
            this.configured = true;
        }
    }

    private void loadConfig() {
        LOG.info("Loading config");
        try {
            ClassLoader contextClassLoader = this.getClass().getClassLoader();
            InputStream resourceAsStream = contextClassLoader.getResourceAsStream(CONFIG_FILENAME);
            Properties config = new Properties();
            config.load(resourceAsStream);

            DB_NAME = config.getProperty("DB_NAME");
            MONGO_HOST= config.getProperty("MONGO_HOST");
            MONGO_PORT = Integer.parseInt(config.getProperty("MONGO_PORT"));
            MONGO_USERNAME = config.getProperty("MONGO_USERNAME");
            MONGO_PASSWORD = config.getProperty("MONGO_PASSWORD");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load config", e);
        }
    }

    private void setupDB() {
        LOG.info("Connecting to MongoDB on " + MONGO_HOST + ":" + MONGO_PORT);
        try {
            mongoClient = new MongoClient(MONGO_HOST, MONGO_PORT);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Couldn't connect to mongo", e);
        }

        LOG.info("Getting db [" + DB_NAME + "]");
        db = mongoClient.getDB(DB_NAME);
        LOG.info("Authenticating with mongo, username [" + MONGO_USERNAME + "]");
        boolean auth = db.authenticate(MONGO_USERNAME, MONGO_PASSWORD.toCharArray());
        LOG.debug("Authenticated: " + auth);
        if (!auth) {
            throw new RuntimeException("Couldn't authenticate with DB");
        }

        items = db.getCollection("contentItems");
    }
}