package statik.route;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import statik.content.ContentItem;
import statik.content.ContentStore;
import statik.util.Http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class EditorRoute extends InternationalisedResourceRoute {

    private static final Logger LOG = LoggerFactory.getLogger(EditorRoute.class);
    private static final String EDITOR_HTML = "wysihtml5/editor.html";
    public static final String HIDDEN_INPUTS_TEMPLATE = "<input type=\"hidden\" name=\"selector\" value=\"%s\" />  \n  <input type=\"hidden\" name=\"path\" value=\"%s\" />";
    private final ContentStore contentStore;

    public EditorRoute(String route, ContentStore contentStore) {
        super(route);
        this.contentStore = contentStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        String encodedSelector = request.queryParams("selector");
        String encodedPath = request.queryParams("path");
        String encodedContent = request.queryParams("content");

        String selector;
        String path;
        String sentContent;
        try {
            selector = URLDecoder.decode(encodedSelector, "utf-8");
            path = URLDecoder.decode(encodedPath, "utf-8");
            sentContent = URLDecoder.decode(encodedContent, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Couldn't decode URI-encoded parameter");
            response.status(400);
            return Http.EMPTY_RESPONSE;
        }

        LOG.trace("GET " + request.raw().getRequestURL() + ", selector [" + selector + "], path [" + path + "], content [" + sentContent + "]");

        ContentItem contentItem = lookupContentItemBy(selector, path);

        String editorView = populateEditorView(selector, path, contentItem, sentContent);
        if (editorView == null) {
            response.status(404);
            return Http.EMPTY_RESPONSE;
        }

        return editorView;
    }

    private ContentItem lookupContentItemBy(String selector, String path) {
        return this.contentStore.findByPathAndSelector(path, selector);
    }

    private String populateEditorView(String selector, String path, ContentItem contentItem, String sentContent) {
        String data = i18n(RESOURCE_ROOT_PATH + EDITOR_HTML);
        if (data == null) {
            return null;
        }

        Document document = Jsoup.parse(data);
        document.outputSettings().escapeMode(Entities.EscapeMode.extended);

        String hiddenFields = String.format(HIDDEN_INPUTS_TEMPLATE, selector, path);

        Elements form = document.select("#editorForm");
        form.append(hiddenFields);

        Elements textareaPreload = document.select("#textarea-preload");
        if (contentItem != null) {
            LOG.debug("Found the content in the DB, passing that to load the editor");
            String content = contentItem.content();
            if (content == null) {
                content = "";
            }
            textareaPreload.first().html(content);
        } else {
            LOG.debug("Couldn't find content in the DB; assume first time we've edited this element, pass back content from page [" + sentContent + "]");
            textareaPreload.first().html(sentContent);
        }

        return document.toString();
    }

}
