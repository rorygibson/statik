package com.example.web.route;

import com.example.web.Main;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

public class CESResourceRoute extends AbstractRoute {

    private static final Logger LOG = Logger.getLogger(CESResourceRoute.class);

    public CESResourceRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        String filename = request.params("file");
        LOG.debug("Request for file, url is [" + request.url() + "], file is [" + filename + "]");

        return writeClasspathFileToResponse(response, filename);
    }
}
