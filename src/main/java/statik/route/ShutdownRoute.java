package statik.route;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import statik.util.Http;

public class ShutdownRoute extends Route {

    private static final Logger LOG = LoggerFactory.getLogger(ShutdownRoute.class);

    public ShutdownRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        LOG.error("System shutting down");
        System.exit(0);
        return Http.EMPTY_RESPONSE;
    }
}
