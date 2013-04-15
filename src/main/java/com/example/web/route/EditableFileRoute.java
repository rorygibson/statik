package com.example.web.route;

import com.example.web.AuthStore;
import com.example.web.ContentItem;
import com.example.web.Database;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public class EditableFileRoute extends AbstractAuthenticatedRoute {

    public static final String AUTHENTICATED_JAVASCRIPT_TO_APPEND = "<script src=\"ces-resources/authenticated.js\" type=\"text/javascript\"></script><script src=\"ces-resources/jquery-getpath.js\" type=\"text/javascript\"></script>";
    public static final String HTML_SUFFIX = ".html";
    private Database database;
    private String fileBase;
    private String namedFile = null;
    private static final Logger LOG = Logger.getLogger(EditableFileRoute.class);

    public EditableFileRoute(Database database, String fileBase, String route, AuthStore authStore) {
        super(route, authStore);
        this.database = database;
        this.fileBase = fileBase;
    }

    public EditableFileRoute(Database database, String fileBase, String route, String namedFile, AuthStore authStore) {
        super(route, authStore);
        this.database = database;
        this.fileBase = fileBase;
        this.namedFile = namedFile;
    }

    @Override
    public Object handle(Request request, Response response) {
        File theFile;
        String path = request.raw().getServletPath();

        if (thisRouteIsBoundToASpecificFile()) {
            theFile = findMySpecificFile();
        } else {
            theFile = findRequestedFileFrom(path);
        }

        if (!theFile.exists()) {
            LOG.warn("File not found [" + theFile.getAbsolutePath() + "]");
            response.status(404);
            return EMPTY_RESPONSE;
        }

        try {
            response.status(200);

            if (mightContainCmsContent(theFile)) {
                LOG.debug("File is candidate for content editing");
                return cesify(path, theFile, hasSession(request));
            }

            LOG.trace("Serving file [" + theFile.getAbsolutePath() + "] straight from disk");
            writeFileToResponse(response, theFile);

        } catch (IOException e) {
            LOG.error("Error reading [" + theFile.getAbsolutePath() + "]");
        }

        return EMPTY_RESPONSE;
    }

    private File findMySpecificFile() {
        String path = fileBase + "/" + this.namedFile;
        LOG.trace("Serving welcome file [" + path + "]");
        return new File(path);
    }

    private File findRequestedFileFrom(String path) {
        String fullPath = fileBase + "/" + path;
        LOG.trace("Request for file; full path to file is [" + fullPath + "]");
        return new File(fullPath);
    }

    private boolean thisRouteIsBoundToASpecificFile() {
        return this.namedFile != null;
    }

    private Document makeEditable(Document doc) {
        doc.body().append(AUTHENTICATED_JAVASCRIPT_TO_APPEND);
        return doc;
    }

    private boolean mightContainCmsContent(File theFile) {
        return theFile.getName().endsWith(HTML_SUFFIX);
    }

    private Document replaceContent(Document doc, String path) {
        Map<String,ContentItem> contentItems = this.database.findForPath(path);

        for (String selector : contentItems.keySet()) {
            ContentItem contentItem = contentItems.get(selector);
            Element el = doc.select(selector).first();
            LOG.debug("trace content with selector [" + selector + "]");
            el.text(contentItem.content());
        }

        return doc;
    }

    private String cesify(String path, File theFile, boolean authenticated) throws IOException {
        LOG.debug("CESifying file ");

        Document doc = Jsoup.parse(FileUtils.readFileToString(theFile));
        doc = replaceContent(doc, path);

        if (authenticated) {
            LOG.debug("Making page editable");
            doc = makeEditable(doc);
        }

        return doc.toString();
    }
}
