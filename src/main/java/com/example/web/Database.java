package com.example.web;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;

public class Database {
    class DB {
        private static final Logger LOG = Logger.getLogger(Main.DB.class);

        private MongoClient mongoClient = null;

        private Main.DB db = null;
        private DBCollection items = null;

        public String DB_NAME = "";
        private String MONGO_HOST = "";
        private int MONGO_PORT = 0;
        private String MONGO_USERNAME = "";
        private String MONGO_PASSWORD = "";


        public boolean isEmpty() {
            int count = items.find().count();
            LOG.info("Number of content items in DB is " + count);
            return count == 0;
        }

        public void setupDB() {
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
}