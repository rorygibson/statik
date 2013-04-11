package com.example.web;

import com.mongodb.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;

public class MongoDatabase implements Database {
    private static final Logger LOG = Logger.getLogger(MongoDatabase.class);

    private MongoClient mongoClient = null;

    private DBCollection items = null;
    private DB db = null;

    public String DB_NAME = "";
    private String MONGO_HOST = "";
    private int MONGO_PORT = 0;
    private String MONGO_USERNAME = "";
    private String MONGO_PASSWORD = "";

    private boolean configured = false;

    @Override
    public boolean isEmpty() {
        int count = items.find().count();
        LOG.info("Number of content items in DB is " + count);
        return count == 0;
    }

    @Override
    public String get(String id) {
        BasicDBObject q = new BasicDBObject("contentId", id);
        DBObject r = items.findOne(q);
        return (r == null) ? null : r.containsField("content") ? r.get("content").toString() : null;
    }

    @Override
    public void insertOrUpdate(String id, String content) {
        LOG.debug("Updating contentItem [" + id + "] with content, length [" + content.length() + "]");

        BasicDBObject queryObject = new BasicDBObject("contentId", id);
        BasicDBObject updateObject = new BasicDBObject("contentId", id).append("content", content);
        WriteResult update = items.update(queryObject, updateObject);

        if (update.getN() == 0) {
            LOG.debug("No rows updated, assume insert");
            items.insert(updateObject);
        } else {
            LOG.debug("Updated in DB");
        }
    }

    @Override
    public void configure(String configFilename) {
        if (!configured) {
            loadConfig(configFilename);
            setupDB();
            this.configured = true;
        }
    }

    private void loadConfig(String filename) {
        LOG.info("Loading config");
        Properties config = PropertiesLoader.loadProperties(filename);

        DB_NAME = config.getProperty("DB_NAME");
        MONGO_HOST= config.getProperty("MONGO_HOST");
        MONGO_PORT = Integer.parseInt(config.getProperty("MONGO_PORT"));
        MONGO_USERNAME = config.getProperty("MONGO_USERNAME");
        MONGO_PASSWORD = config.getProperty("MONGO_PASSWORD");
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