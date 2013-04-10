package com.example.web;

import com.mongodb.BasicDBObject;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class Main implements spark.servlet.SparkApplication {

    private static final Logger log = Logger.getLogger(Main.class);

    private static final String MESSAGE_BUNDLE_NAME = "messages.properties";
    private Database database;

    public static void main(String[] args) {
        new Main().init();
    }

    @Override
    public void init() {
        log.debug("init()");
        this.database = new Database();
        this.database.configure();

        if (database.isEmpty()) {
            loadMessages();
        }

        log.info("Setting up routes");

        Spark.get(new Route("/content/:id") {
            @Override
            public Object handle(Request request, Response response) {
                log.debug("GET " + request.url());
                String id = request.params("id");

                String content = database.get(id);

                log.debug("Content size [" + content.length() + "]");
                return content;
            }
        });

        Spark.post(new Route("/content/:id") {
            @Override
            public Object handle(Request request, Response response) {
                log.debug("POST " + request.url());
                Map<String, String[]> parameterMap = request.raw().getParameterMap();

                String id = request.params("id");
                String newContent = parameterMap.get("content")[0];

                database.insert(id, newContent);

                response.status(200);
                return "OK";
            }
        });
    }


    private void loadMessages() {
        log.info("Loading messages into empty DB");
        Properties content = new Properties();
        try {
            ClassLoader contextClassLoader = this.getClass().getClassLoader();
            InputStream resourceAsStream = contextClassLoader.getResourceAsStream(MESSAGE_BUNDLE_NAME);

            content.load(resourceAsStream);

            log.info("Loaded " + content.size() + " as properties");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load messages", e);
        }

        for (Object key : content.keySet()) {
            String value = content.getProperty(key.toString());
            this.database.insert(key.toString(), value);
            log.debug("Inserted item to mongo");
        }
    }
}
