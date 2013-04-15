package statik.route;

import statik.ContentItem;
import statik.Database;
import statik.Http;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class ContentRoute extends Route {

    private static final Logger LOG = Logger.getLogger(ContentRoute.class);
    private final Database database;

    public ContentRoute(Database database, String route) {
        super(route);
        this.database = database;
    }

    @Override
    public Object handle(Request request, Response response) {
        Map<String, String[]> parameterMap = request.raw().getParameterMap();

        String newContent = parameterMap.get(ContentItem.CONTENT)[0];
        String selector = parameterMap.get(ContentItem.SELECTOR)[0];
        String path = parameterMap.get(ContentItem.PATH)[0];

        LOG.debug("POST with selector [" + selector + "], path [" + path + "], content length [" + newContent.length() + "]");

        database.insertOrUpdate(new ContentItem(path, selector, newContent));

        response.status(200);
        return Http.EMPTY_RESPONSE;
    }
}
