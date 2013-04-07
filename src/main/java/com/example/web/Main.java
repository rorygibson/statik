package com.example.web;

import java.io.File;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class Main implements spark.servlet.SparkApplication {

    public Main() {
        setupRoutes();
    }

    public void setupRoutes() {
        Spark.get(new Route("/") {

            @Override
            public Object handle(Request request, Response response) {
                String name = request.queryParams("name");
                if (name == null || name.equals("")) {
                    name = "Anonymous";
                }

                return String.format("Hello, %s, isn't it a lovely day?", name);
            }

        });
    }


    public static void main(String[] args) {
        new Main();
    }

    @Override
    public void init() {
        setupRoutes();
    }
}
