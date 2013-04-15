package com.example.web.route;

import com.example.web.Http;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.InputStream;

public class CESResourceRoute extends Route {

    private static final Logger LOG = Logger.getLogger(CESResourceRoute.class);
    private static final String RESOURCE_ROOT_PATH = "/ces-resources/";

    public CESResourceRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        String filename = request.params("file");
        LOG.trace("Request for file, path is [" + request.url() + "], file is [" + filename + "]");

        return writeClasspathFileToResponse(response, filename);
    }


    protected Object writeClasspathFileToResponse(Response response, String filename) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(RESOURCE_ROOT_PATH + filename);
        try {
            IOUtils.copy(resourceAsStream, response.raw().getOutputStream());
            response.status(200);
            return Http.EMPTY_RESPONSE;
        } catch (IOException e) {
            LOG.error("Error retrieving resource", e);
        }
        response.status(404);
        return Http.EMPTY_RESPONSE;
    }
}
