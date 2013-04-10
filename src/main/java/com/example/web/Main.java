package com.example.web;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;

public class Main implements spark.servlet.SparkApplication {

    private MongoClient mongoClient = null;
    private DB db = null;
    private DBCollection items = null;

    public static void main(String[] args) {
        new Main().init();
    }

    @Override
    public void init() {
        System.out.println("init()");
        setupDB();

        if (dbEmpty()) {
            loadMessages();
        }

        //Spark.staticFileRoute("public");

        System.out.println("Setting up routes");

        Spark.get(new Route("/content/:id") {
            @Override
            public Object handle(Request request, Response response) {
                System.out.println("GET " + request.url());
                String id = request.params("id");

                BasicDBObject dbObject = new BasicDBObject("contentId", id);

                return items.findOne(dbObject).get("content");
            }
        });

        Spark.post(new Route("/content/:id") {
            @Override
            public Object handle(Request request, Response response) {
                System.out.println("POSTed " + request.url());
                Map<String, String[]> parameterMap = request.raw().getParameterMap();

                String id = request.params("id");
                String newContent = parameterMap.get("content")[0];

                System.out.println("Updating contentItem " + id + "with content [" + newContent + "]");

                BasicDBObject queryObject = new BasicDBObject("contentId", id);
                BasicDBObject updateObject = new BasicDBObject("contentId", id).append("content", newContent);
                items.update(queryObject, updateObject);

                response.status(200);
                return "OK";
            }
        });
    }

    private boolean dbEmpty() {
        int count = items.find().count();
        System.out.println("Number of content items is " + count);
        return count == 0;
    }

    private void setupDB() {
        System.out.println("Setting up DB");
        try {
            mongoClient = new MongoClient( "hellojava-brightnorth-mdb-0.azva.dotcloud.net", 32858 );
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        db = mongoClient.getDB( "contentdb" );
        db.authenticate("root", "jIUexSnZWFMjer4GSsWm".toCharArray());

        items = db.getCollection("contentItems");
    }

    private void loadMessages() {
        System.out.println("Loading messages into DB");
        Properties content = new Properties();
        try {
            ClassLoader contextClassLoader = this.getClass().getClassLoader();
            InputStream resourceAsStream = contextClassLoader.getResourceAsStream("messages.properties");

            content.load(resourceAsStream);

            System.out.println("Loaded " + content.size() + " as properties");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Object key : content.keySet()) {
            String value = content.getProperty(key.toString());
            BasicDBObject dbObj = new BasicDBObject("contentId", key.toString()).append("content", value);
            items.insert(dbObj);
            System.out.println("Inserted item to mongo");
        }
    }
}
