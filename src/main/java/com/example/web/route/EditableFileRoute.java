package com.example.web.route;

import com.example.web.AuthStore;
import com.example.web.ContentItem;
import com.example.web.Database;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

    private final String SELECTOR = "*[data-content-id]";
    private final String ATTRIBUTE_NAME = "data-content-id";

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

        if (thisRouteIsBoundToASpecificFile()) {
            theFile = findMySpecificFile();
        } else {
            theFile = findRequestedFileFrom(request);
        }

        if (!theFile.exists()) {
            LOG.warn("File not found [" + theFile.getAbsolutePath() + "]");
            response.status(404);
            return "File not found";
        }

        try {
            response.status(200);

            if (mightContainCmsContent(theFile)) {
                LOG.debug("File is candidate for content editing");
                return cmsify(theFile, hasSession(request));
            }

            LOG.debug("Serving file [" + theFile.getAbsolutePath() + "] straight from disk");
            writeFileToResponse(response, theFile);

        } catch (IOException e) {
            LOG.error("Error reading [" + theFile.getAbsolutePath() + "]");
        }

        return "";
    }

    private File findMySpecificFile() {
        File theFile;
        theFile = new File(fileBase + "/" + this.namedFile);
        LOG.debug("Serving welcome file [" + theFile.getAbsolutePath() + "]");
        return theFile;
    }

    private File findRequestedFileFrom(Request request) {
        File theFile;
        String fullPath = fileBase + "/" + request.raw().getServletPath();
        theFile = new File(fullPath);
        LOG.debug("Request for file, url is [" + request.url() + "], full path to file is [" + fullPath + "]");
        return theFile;
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

    private Document replaceContent(Document doc) {
        Map<String,ContentItem> contentItems = this.database.findAll();

        for (String selector : contentItems.keySet()) {
            ContentItem contentItem = contentItems.get(selector);
            Element el = doc.select(selector).first();
            LOG.debug("Replacing content with selector [" + selector + "]");
            el.text(contentItem.content());
        }

        return doc;
    }

    private Object cmsify(File theFile, boolean authenticated) throws IOException {
        LOG.debug("CESifying file");

        Document doc;
        doc = Jsoup.parse(FileUtils.readFileToString(theFile));
        doc = replaceContent(doc);

        if (authenticated) {
            LOG.debug("Making page editable");
            doc = makeEditable(doc);
        }

        return doc.toString();
    }
}
