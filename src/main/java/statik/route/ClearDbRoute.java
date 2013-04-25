package statik.route;

import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;
import statik.content.ContentStore;
import statik.session.SessionStore;
import statik.util.Http;

public class ClearDbRoute extends Route {

    private static final Logger LOG = Logger.getLogger(ClearDbRoute.class);
    private final ContentStore contentStore;
    private final SessionStore sessionStore;

    public ClearDbRoute(String route, ContentStore db, SessionStore ss) {
        super(route);
        this.contentStore = db;
        this.sessionStore = ss;
    }

    @Override
    public Object handle(Request request, Response response) {
        this.contentStore.clearContentItems();
        this.sessionStore.deleteAllSessions();
        return Http.EMPTY_RESPONSE;
    }
}
