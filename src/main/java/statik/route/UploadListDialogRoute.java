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
import java.util.ArrayList;
import java.util.List;

public class UploadListDialogRoute extends ThymeLeafResourceRoute {

    private static final Logger LOG = LoggerFactory.getLogger(UploadListDialogRoute.class);
    public static final String HIDDEN_INPUTS_TEMPLATE = "<input type=\"hidden\" name=\"selector\" value=\"%s\" />  \n  <input type=\"hidden\" name=\"domain\" value=\"%s\" /> \n <input type=\"hidden\" name=\"path\" value=\"%s\" /> ";

    public UploadListDialogRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        String encodedSelector = request.queryParams("selector");
        String encodedPath = request.queryParams("path");
        String encodedDomain = request.queryParams("domain");
        String encodedLanguage = request.queryParams("language");

        String selector;
        String path;
        String domain;
        String language;

        try {
            selector = URLDecoder.decode(encodedSelector, "utf-8");
            path = URLDecoder.decode(encodedPath, "utf-8");
            domain = URLDecoder.decode(encodedDomain, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Couldn't decode URI-encoded parameter");
            response.status(400);
            return Http.EMPTY_RESPONSE;
        }


        LOG.trace("GET " + request.raw().getRequestURL() + ", selector [" + selector + "]");
        String data = processWithThymeLeaf(PathsAndRoutes.UPLOAD_DIALOG_VIEWNAME);

        Document document = Jsoup.parse(data);
        document.outputSettings().escapeMode(Entities.EscapeMode.extended);

        String hiddenFields = String.format(HIDDEN_INPUTS_TEMPLATE, selector, domain, path);

        Elements form = document.select("#imgChooserForm");
        form.append(hiddenFields);

        return document.toString();
    }

}
