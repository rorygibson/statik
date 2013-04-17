package statik.route;

import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;
import statik.AuthStore;
import statik.Database;
import statik.Http;
import statik.SessionStore;

public class ClearDbRoute extends Route {

    private static final Logger LOG = Logger.getLogger(ClearDbRoute.class);
    private final Database database;

    public ClearDbRoute(String route, Database db) {
        super(route);
        this.database = db;
    }

    @Override
    public Object handle(Request request, Response response) {
        this.database.clearContentItems();
        return Http.EMPTY_RESPONSE;
    }
}
