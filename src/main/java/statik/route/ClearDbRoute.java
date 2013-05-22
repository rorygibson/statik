package statik.route;

import spark.Request;
import spark.Response;
import spark.Route;
import statik.auth.AuthStore;
import statik.content.ContentStore;
import statik.session.SessionStore;
import statik.util.Http;

public class ClearDbRoute extends Route {

    private final ContentStore contentStore;
    private final SessionStore sessionStore;
    private final AuthStore authStore;

    public ClearDbRoute(String route, ContentStore db, SessionStore ss, AuthStore authStore) {
        super(route);
        this.contentStore = db;
        this.sessionStore = ss;
        this.authStore = authStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        this.contentStore.clearContentItems();
        this.sessionStore.deleteAllSessions();
        this.authStore.deleteAllUsersExceptDefault();
        return Http.EMPTY_RESPONSE;
    }
}
