package statik.route;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import spark.Request;
import spark.Response;
import spark.Route;
import statik.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Map;


public class EditableFileRoute extends Route {

    public static final String AUTHENTICATED_JAVASCRIPT_TO_APPEND = "<script src=\"ces-resources/authenticated.js\" type=\"text/javascript\"></script><script src=\"ces-resources/getpath.js\" type=\"text/javascript\"></script>";
    public static final String HTML_SUFFIX = ".html";
    private final AuthStore authStore;
    private final SessionStore sessionStore;
    private Database database;
    private String fileBase;
    private String namedFile = null;
    private static final Logger LOG = Logger.getLogger(EditableFileRoute.class);

    public EditableFileRoute(Database database, String fileBase, String route, AuthStore authStore, SessionStore sessionStore) {
        super(route);
        this.database = database;
        this.fileBase = fileBase;
        this.authStore = authStore;
        this.sessionStore = sessionStore;
    }

    public EditableFileRoute(Database database, String fileBase, String route, String namedFile, AuthStore authStore, SessionStore sessionStore) {
        super(route);
        this.database = database;
        this.fileBase = fileBase;
        this.namedFile = namedFile;
        this.authStore = authStore;
        this.sessionStore = sessionStore;
    }

    @Override
    public Object handle(Request request, Response response) {
        HttpServletRequest httpReq = request.raw();
        String path = httpReq.getPathInfo() == null ? httpReq.getServletPath() : httpReq.getPathInfo();

        LOG.debug("GET " + path);

        File theFile;
        if (thisRouteIsBoundToASpecificFile()) {
            theFile = findMySpecificFile();
        } else {
            theFile = findRequestedFileFrom(path);
        }

        if (!theFile.exists()) {
            LOG.warn("File not found [" + theFile.getAbsolutePath() + "]");
            response.status(404);
            return Http.EMPTY_RESPONSE;
        }

        try {
            response.status(200);

            if (mightContainCmsContent(theFile)) {
                LOG.debug("File is candidate for content editing");
                return cesify(path, theFile, sessionStore.hasSession(Http.sessionFrom(request)));
            }

            LOG.trace("Serving file [" + theFile.getAbsolutePath() + "] straight from disk");
            writeFileToResponse(response, theFile);

        } catch (IOException e) {
            LOG.error("Error reading [" + theFile.getAbsolutePath() + "]");
        }

        return Http.EMPTY_RESPONSE;
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
        Map<String, ContentItem> contentItems = this.database.findForPath(path);

        for (String selector : contentItems.keySet()) {
            ContentItem contentItem = contentItems.get(selector);
            Element el = doc.select(selector).first();
            LOG.trace("Replaced content with selector [" + selector + "]");
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


    public void writeFileToResponse(Response response, File theFile) throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(theFile);
        response.raw().getOutputStream().write(bytes);
    }

}
