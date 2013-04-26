package statik.route;

import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;
import statik.util.Http;

public class ShutdownRoute extends Route {

    private static final Logger LOG = Logger.getLogger(ShutdownRoute.class);

    public ShutdownRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        LOG.fatal("System shutting down");
        System.exit(0);
        return Http.EMPTY_RESPONSE;
    }
}
