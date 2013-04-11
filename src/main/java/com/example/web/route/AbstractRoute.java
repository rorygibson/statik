package com.example.web.route;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import spark.Response;
import spark.Route;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractRoute extends Route {

    private static final Logger LOG = Logger.getLogger(AbstractRoute.class);

    public AbstractRoute(String route) {
        super(route);
    }

    protected Object writeClasspathFileToResponse(Response response, String filename) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("/ces-resources/" + filename);
        try {
            IOUtils.copy(resourceAsStream, response.raw().getOutputStream());
            response.status(200);
            return "";
        } catch (IOException e) {
            LOG.error("Error retrieving resource", e);
        }
        response.status(404);
        return "ERROR";
    }

    public void writeFileToResponse(Response response, File theFile) throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(theFile);
        response.raw().getOutputStream().write(bytes);
    }

}
