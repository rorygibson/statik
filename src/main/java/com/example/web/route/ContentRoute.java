package com.example.web.route;

import com.example.web.Database;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

import java.util.Map;

public class ContentRoute extends AbstractRoute {

    private static final Logger LOG = Logger.getLogger(ContentRoute.class);
    private final Database database;

    public ContentRoute(Database database, String route) {
        super(route);
        this.database = database;
    }

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
}
