package com.example.web.route;

import com.example.web.AuthStore;
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

        if (this.namedFile == null) {
            String filename = request.raw().getServletPath();
            String fullPath = fileBase + "/" + filename;
            theFile = new File(fullPath);
            LOG.debug("Request for file, url is [" + request.url() + "], full path to file is [" + fullPath + "]");
        } else {
            theFile = new File(fileBase + "/" + this.namedFile);
            LOG.debug("Serving welcome file [" + theFile.getAbsolutePath() + "]");
        }

        if (theFile.exists()) {
            try {
                response.status(200);

                if (mightContainCmsContent(theFile)) {
                    LOG.debug("File is candidate for content editing");
                    return cmsify(theFile, hasSession(request));
                }

                LOG.debug("Serving file [" + theFile.getAbsolutePath() + "] straight from disk");
                writeFileToResponse(response, theFile);
                return "";

            } catch (IOException e) {
                LOG.error("Error reading [" + theFile.getAbsolutePath() + "]");
            }
        }

        LOG.warn("File not found [" + theFile.getAbsolutePath() + "]");
        response.status(404);
        return "File not found";
    }


    private Document makeEditable(Document doc) {
        doc.body().append(AUTHENTICATED_JAVASCRIPT_TO_APPEND);
        return doc;
    }

    private boolean mightContainCmsContent(File theFile) {
        return theFile.getName().endsWith(HTML_SUFFIX);
    }

    private Document replaceContent(Document doc) {
        Elements elements = doc.select(SELECTOR);

        for (Element el : elements) {
            String id = el.attr(ATTRIBUTE_NAME);

            LOG.debug("Found replaceable content item with id [" + id + "]");
            String newContent = this.database.get(id);

            if (newContent != null) {
                el.text(newContent);
            }
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
