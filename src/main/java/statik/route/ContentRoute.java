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
        Map<String, String[]> parameterMap = request.raw().getParameterMap();

        String newContent = parameterMap.get(ContentItem.CONTENT)[0];
        String domain = parameterMap.get(ContentItem.DOMAIN)[0];
        String selector = parameterMap.get(ContentItem.SELECTOR)[0];
        String path = parameterMap.get(ContentItem.PATH)[0];
        String lang = parameterMap.get(ContentItem.LANGUAGE)[0];

        LOG.debug("POST with selector [" + selector + "], domain [" + domain + "], path [" + path + "], content [" + newContent + "], lang [" + lang + "]");

        ContentItem contentItem = new ContentItem(domain, path, selector, newContent, false, false, Language.from(lang));
        contentStore.insertOrUpdate(contentItem);

        response.status(200);
        return Http.OK_RESPONSE;
    }
}
