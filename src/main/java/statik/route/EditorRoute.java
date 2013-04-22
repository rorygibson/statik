package statik.route;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import spark.Request;
import spark.Response;
import statik.ContentItem;
import statik.Database;
import statik.Http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class EditorRoute extends CESResourceRoute {

    private static final Logger LOG = Logger.getLogger(EditorRoute.class);
    private static final String EDITOR_HTML = "wysihtml5/editor.html";
    private final Database database;

    public EditorRoute(String route, Database database) {
        super(route);
        this.database = database;
    }

    @Override
    public Object handle(Request request, Response response) {
        String encodedSelector = request.queryParams("selector");
        String encodedPath = request.queryParams("path");
        String encodedContent = request.queryParams("content");

        String selector = "";
        String path = "";
        String sentContent = "";
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
        ContentItem contentItem = this.database.findByPathAndSelector(path, selector);

        String data = null;
        String filePath = RESOURCE_ROOT_PATH + EDITOR_HTML;
        try {
            data = fileAsString(filePath);
        } catch (IOException e) {
            LOG.warn("Couldn't load file " + filePath);
            response.status(404);
            return Http.EMPTY_RESPONSE;
        }

        Document document = Jsoup.parse(data);
        String template = "<input type=\"hidden\" name=\"selector\" value=\"%s\" />  \n  <input type=\"hidden\" name=\"path\" value=\"%s\" />";
        String replaced = String.format(template, selector, path);

        Elements form = document.select("#editorForm");
        form.append(replaced);

        Elements textarea = document.select("textarea");
        if (contentItem != null) {
            LOG.debug("Found the content in the DB, passing that to load the editor");
            textarea.first().text(contentItem.content());
        } else {
            LOG.debug("Couldn't find content in the DB; assume first time we've edited this element, pass back content from page [" + sentContent + "]");
            textarea.first().text(sentContent);
        }

        return document.toString();
    }

    private String fileAsString(String file) throws IOException {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(file);
        String data = "";
        data = IOUtils.toString(resourceAsStream);
        return data;
    }

}
