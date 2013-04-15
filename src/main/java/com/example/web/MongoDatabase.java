package com.example.web;

import com.mongodb.*;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;
import java.util.*;

public class MongoDatabase implements Database {
    private static final Logger LOG = Logger.getLogger(MongoDatabase.class);
    public static final String COLLECTION_NAME = "contentItems";

    private MongoClient mongoClient = null;

    private DBCollection items = null;
    private DB db = null;

    private String dbName = "";
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
    public void insertOrUpdate(ContentItem contentItem) {
        LOG.debug("Updating with content, size [" + contentItem.size() + "] and selector [" + contentItem.selector() + "]");

        BasicDBObject queryObject = new BasicDBObject(ContentItem.SELECTOR, contentItem.selector());
        BasicDBObject updateObject = new BasicDBObject(ContentItem.SELECTOR, contentItem.selector()).append(ContentItem.CONTENT, contentItem.content()).append(ContentItem.PATH
                , contentItem.path());
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
        if (configured) {
            return;
        }

        loadConfig(configFilename);

        LOG.info("Connecting to MongoDB on " + mongoHost + ":" + mongoPort);
        this.mongoClient = mongoClientFor(mongoHost, mongoPort);
        this.db = mongoClient.getDB(dbName);

        boolean auth = db.authenticate(mongoUsername, mongoPassword.toCharArray());
        if (!auth) {
            throw new RuntimeException("Couldn't authenticate with MongoDB");
        }

        this.items = db.getCollection(COLLECTION_NAME);
        this.configured = true;
    }

    @Override
    public Map<String, ContentItem> findForPath(String path) {
        BasicDBObject query = new BasicDBObject(ContentItem.PATH, path);
        DBCursor cursor = this.items.find(query);

        Map<String, ContentItem> items = new HashMap<String, ContentItem>();
        while (cursor.hasNext()) {
            DBObject dbObject = cursor.next();
            String content = dbObject.get(ContentItem.CONTENT).toString();
            String selector = dbObject.get(ContentItem.SELECTOR).toString();
            String itemPath = dbObject.get(ContentItem.PATH).toString();
            items.put(selector, new ContentItem(itemPath, selector, content));
        }
        return items;
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

    private MongoClient mongoClientFor(String host, int port) {
        try {
            return new MongoClient(host, port);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Couldn't connect to MongoDB", e);
        }
    }
}