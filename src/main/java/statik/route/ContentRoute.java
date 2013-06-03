package statik.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import statik.content.ContentItem;
import statik.content.ContentStore;
import statik.util.Http;
import statik.util.Language;

import java.util.Map;

public class ContentRoute extends Route {

    private static final Logger LOG = LoggerFactory.getLogger(ContentRoute.class);
    private final ContentStore contentStore;

    public ContentRoute(String route, ContentStore contentStore) {
        super(route);
        this.contentStore = contentStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        String newContent = parameterFrom(ContentItem.CONTENT, request);
        String domain = parameterFrom(ContentItem.DOMAIN, request);
        String selector = parameterFrom(ContentItem.SELECTOR, request);
        String path = parameterFrom(ContentItem.PATH, request);
        String lang = parameterFrom(ContentItem.LANGUAGE, request);

        LOG.debug("POST with selector [" + selector + "], domain [" + domain + "], path [" + path + "], content [" + newContent + "], lang [" + lang + "]");

        ContentItem contentItem = new ContentItem(domain, path, selector, newContent, false, false, Language.from(lang));
        contentStore.insertOrUpdate(contentItem);

        response.status(200);
        return Http.OK_RESPONSE;
    }


    private String parameterFrom(String param, Request r) {
        Map<String, String[]> parameterMap = r.raw().getParameterMap();
        if (parameterMap.containsKey(param)) {
            return parameterMap.get(param)[0];
        }
        return "";
    }
}
