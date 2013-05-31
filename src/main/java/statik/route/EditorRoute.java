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
import statik.util.Language;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class EditorRoute extends ThymeLeafResourceRoute {

    private static final Logger LOG = LoggerFactory.getLogger(EditorRoute.class);
    public static final String HIDDEN_INPUTS_TEMPLATE = "<input type=\"hidden\" name=\"selector\" value=\"%s\" />  \n  <input type=\"hidden\" name=\"domain\" value=\"%s\" /> \n <input type=\"hidden\" name=\"path\" value=\"%s\" /> <input type=\"hidden\" name=\"language\" value=\"%s\" /> ";
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
        String encodedDomain = request.queryParams("domain");
        String encodedLanguage = request.queryParams("language");

        String selector;
        String path;
        String sentContent;
        String domain;
        String language;

        try {
            selector = URLDecoder.decode(encodedSelector, "utf-8");
            path = URLDecoder.decode(encodedPath, "utf-8");
            sentContent = URLDecoder.decode(encodedContent, "utf-8");
            domain = URLDecoder.decode(encodedDomain, "utf-8");
            language = URLDecoder.decode(encodedLanguage, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Couldn't decode URI-encoded parameter");
            response.status(400);
            return Http.EMPTY_RESPONSE;
        }

        LOG.trace("GET " + request.raw().getRequestURL() + ", selector [" + selector + "], domain [" + domain + "], path [" + path + "], language [" + language + "] and content [" + sentContent + "]");

        ContentItem contentItem = lookupContentItemBy(selector, domain, path, Language.from(language));

        String editorView = populateEditorView(selector, domain, path, contentItem, sentContent, language);
        if (editorView == null) {
            response.status(404);
            return Http.EMPTY_RESPONSE;
        }

        return editorView;
    }

    private ContentItem lookupContentItemBy(String selector, String domain, String path, Language lang) {
        return this.contentStore.findBy(domain, path, selector, lang);
    }

    private String populateEditorView(String selector, String domain, String path, ContentItem contentItem, String sentContent, String language) {
        String data = processWithThymeLeaf(PathsAndRoutes.EDITOR_VIEWNAME);

        if (data == null) {
            return null;
        }

        Document document = Jsoup.parse(data);
        document.outputSettings().escapeMode(Entities.EscapeMode.extended);

        String hiddenFields = String.format(HIDDEN_INPUTS_TEMPLATE, selector, domain, path, language);

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
