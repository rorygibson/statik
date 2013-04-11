package com.example.web;

import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.util.Map;
import java.util.Properties;

public class Main implements spark.servlet.SparkApplication {

    private static final Logger LOG = Logger.getLogger(Main.class);

    private static final String MESSAGE_BUNDLE_NAME = "messages.properties";
    private Database database;

    public static void main(String[] args) {
        new Main().init();
    }

    @Override
    public void init() {
        LOG.debug("init()");
        this.database = new MongoDatabase();
        this.database.configure();

        if (database.isEmpty()) {
            loadMessages();
        }

        LOG.info("Setting up routes");

        Spark.staticFileRoute("public");

        Spark.get(new Route("/content/:id") {
            @Override
            public Object handle(Request request, Response response) {
                LOG.debug("GET " + request.url());
                String id = request.params("id");

                String content = database.get(id);

                LOG.debug("Content size for id [" + id + "] is [" + content.length() + "]");
                return content;
            }
        });

        Spark.post(new Route("/content/:id") {
            @Override
            public Object handle(Request request, Response response) {
                Map<String, String[]> parameterMap = request.raw().getParameterMap();

                String id = request.params("id");
                String newContent = parameterMap.get("content")[0];
                String selector = parameterMap.get("selector")[0];

                LOG.debug("POST to [" + request.url() + "], + id [" + id + "], selector [" + selector + "], content length [" + newContent.length() + "]");

                database.insertOrUpdate(id, newContent);

                response.status(200);
                return "OK";
            }
        });
    }


    private void loadMessages() {
        LOG.info("Loading messages into DB");
        Properties content = new PropertiesLoader().loadProperties(MESSAGE_BUNDLE_NAME);

        for (Object key : content.keySet()) {
            String value = content.getProperty(key.toString());
            this.database.insertOrUpdate(key.toString(), value);
            LOG.debug("Inserted item to database");
        }
    }

}
