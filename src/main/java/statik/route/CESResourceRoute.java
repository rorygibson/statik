package statik.route;

import statik.util.Http;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.InputStream;

public class CESResourceRoute extends Route {

    private static final Logger LOG = Logger.getLogger(CESResourceRoute.class);
    protected static final String RESOURCE_ROOT_PATH = "statik-resources/";

    public CESResourceRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        String filename = request.splat()[0];
        LOG.trace("Request for file, path is [" + request.url() + "], file is [" + filename + "]");

        return writeClasspathFileToResponse(response, filename);
    }


    protected Object writeClasspathFileToResponse(Response response, String filename) {
        String filePath = RESOURCE_ROOT_PATH + filename;
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(filePath);
        try {
            IOUtils.copy(resourceAsStream, response.raw().getOutputStream());
        } catch (IOException e) {
            LOG.error("Error retrieving resource", e);
            response.status(404);
        }
        return Http.EMPTY_RESPONSE;
    }
}
