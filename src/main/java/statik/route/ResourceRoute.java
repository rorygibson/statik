package statik.route;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import statik.util.Http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ResourceRoute extends Route {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceRoute.class);
    public static final int EXPIRY_IN_MONTHS = 2;

    public ResourceRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        String filename = request.splat()[0];
        LOG.trace("Request for file, path is [" + request.url() + "], file is [" + filename + "]");

        if (testMode()) {
            setCacheable(response);
        }
        return writeClasspathFileToResponse(response, filename);
    }

    private boolean testMode() {
        return !Boolean.getBoolean("testMode");
    }

    private static void setCacheable(Response r) {
        final Calendar inTwoMonths = new GregorianCalendar();
        inTwoMonths.setTime(new Date());
        inTwoMonths.add(Calendar.MONTH, EXPIRY_IN_MONTHS);
        r.raw().setDateHeader("Expires", inTwoMonths.getTimeInMillis());
    }

    protected Object writeClasspathFileToResponse(Response response, String filename) {
        String filePath = PathsAndRoutes.RESOURCE_ROOT_PATH + filename;
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
