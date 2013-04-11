package com.example.web;

import com.mongodb.*;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;
import java.util.Properties;

public class MongoDatabase implements Database {
    private static final Logger LOG = Logger.getLogger(MongoDatabase.class);
    public static final String COLLECTION_NAME = "contentItems";
    public static final String CONTENT_ID = "contentId";
    public static final String CONTENT = "content";

    private MongoClient mongoClient = null;

    private DBCollection items = null;
    private DB db = null;

    public String dbName = "";
    private String mongoHost = "";
    private int mongoPort = 0;
    private String mongoUsername = "";
    private String mongoPassword = "";

    private boolean configured = false;

    @Override
    public boolean isEmpty() {
        int count = items.find().count();
        return count == 0;
    }

    @Override
    public String get(String id) {
        BasicDBObject q = new BasicDBObject(CONTENT_ID, id);
        DBObject r = items.findOne(q);
        return (r == null) ? null : r.containsField(CONTENT) ? r.get(CONTENT).toString() : null;
    }

    @Override
    public void insertOrUpdate(String id, String content) {
        LOG.debug("Updating contentItem [" + id + "] with content, length [" + content.length() + "]");

        BasicDBObject queryObject = new BasicDBObject(CONTENT_ID, id);
        BasicDBObject updateObject = new BasicDBObject(CONTENT_ID, id).append(CONTENT, content);
        WriteResult update = items.update(queryObject, updateObject);

        if (update.getN() == 0) {
            LOG.debug("No rows updated, trying insert");
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

        dbName = config.getProperty("dbName");
        mongoHost = config.getProperty("mongoHost");
        mongoPort = Integer.parseInt(config.getProperty("mongoPort"));
        mongoUsername = config.getProperty("mongoUsername");
        mongoPassword = config.getProperty("mongoPassword");
    }

    private void setupDB() {
        LOG.info("Connecting to MongoDB on " + mongoHost + ":" + mongoPort);

        try {
            mongoClient = new MongoClient(mongoHost, mongoPort);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Couldn't connect to MongoDB", e);
        }

        db = mongoClient.getDB(dbName);
        boolean auth = db.authenticate(mongoUsername, mongoPassword.toCharArray());
        if (!auth) {
            throw new RuntimeException("Couldn't authenticate with MongoDB");
        }

        items = db.getCollection(COLLECTION_NAME);
    }
}