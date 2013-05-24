package statik.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import statik.content.ContentItem;
import statik.content.ContentStore;
import statik.util.Http;

import java.util.Map;

public class MakeContentLiveRoute extends Route {

    private static final Logger LOG = LoggerFactory.getLogger(MakeContentLiveRoute.class);
    private ContentStore contentStore;

    public MakeContentLiveRoute(String route, ContentStore contentStore) {
        super(route);
        this.contentStore = contentStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        Map<String, String[]> parameterMap = request.raw().getParameterMap();
        String domain = parameterMap.get(ContentItem.DOMAIN)[0];
        String path = parameterMap.get(ContentItem.PATH)[0];

        LOG.info("Making the content live for page [" + path + "]");
        this.contentStore.makeContentLiveFor(domain, path);

        return Http.OK_RESPONSE;
    }
}
