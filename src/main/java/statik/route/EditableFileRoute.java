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

    private static final String JQUERY_CSS = "<link href=\"/statik-resources/jquery-ui/css/jquery-ui-1.10.2.custom.min.css\" rel=\"stylesheet\" />";
    private static final String JQUERY_JS = "<script src=\"/statik-resources/jquery-1.9.1.js\" type=\"text/javascript\"></script><script src=\"/statik-resources/jquery-ui/js/jquery-ui-1.10.2.custom.min.js\" type=\"text/javascript\"></script>";
    private static final String AUTH_JS = "<script src=\"/statik-resources/authenticated.js\" type=\"text/javascript\"></script><script src=\"/statik-resources/authenticated-binding.js\" type=\"text/javascript\"></script><script src=\"statik-resources/dom.js\" type=\"text/javascript\"></script><script src=\"/statik-resources/getpath.js\" type=\"text/javascript\"></script>";
    private static final String MENU_JS = "<script src=\"/statik-resources/jquery.contextmenu.r2.packed.js\" type=\"text/javascript\"></script>";
    private static final String LOGOUT_BOX_HTML = "<div id=\"ces-auth-box\" style=\"position:absolute; top:20px; right:20px; border: solid lightgrey 1px; background-color: lightgray; border-radius: 4px; padding: 5px\"><a style=\"color: blue\" href=\"/logout\">Log out</a></div>";
    private static final String EDITOR_HTML = "<div id=\"statik-editor-dialog\"></div>";
    private static final String MENU_HTML = "   <div style=\"display:none\" class=\"contextMenu\" id=\"editMenu\">\n" +
            "      <ul>\n" +
            "        <li id=\"edit\"><img src=\"/statik-resources/edit.png\" /> Edit </li>\n" +
            "      </ul>\n" +
            "    </div>";
    private static final String HTML_SUFFIX = ".html";
    private final SessionStore sessionStore;
    private Database database;
    private String fileBase;
    private String namedFile = null;
    private static final Logger LOG = Logger.getLogger(EditableFileRoute.class);

    public EditableFileRoute(Database database, String fileBase, String route, SessionStore sessionStore) {
        super(route);
        this.database = database;
        this.fileBase = fileBase;
        this.sessionStore = sessionStore;
    }

    public EditableFileRoute(Database database, String fileBase, String route, String namedFile, SessionStore sessionStore) {
        super(route);
        this.database = database;
        this.fileBase = fileBase;
        this.namedFile = namedFile;
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
        String fullPath = fileBase + path;
        LOG.trace("Request for file; full path to file is [" + fullPath + "]");
        return new File(fullPath);
    }

    private boolean thisRouteIsBoundToASpecificFile() {
        return this.namedFile != null;
    }

    private Document makeEditable(Document doc) {
        doc.head().append(JQUERY_CSS);
        doc.body().append(LOGOUT_BOX_HTML);
        doc.body().append(EDITOR_HTML);
        doc.body().append(MENU_HTML);
        doc.body().append(JQUERY_JS);
        doc.body().append(AUTH_JS);
        doc.body().append(MENU_JS);
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
            el.html(contentItem.content());
            LOG.trace("Replaced element with selector [" + selector + "] with content [" + contentItem.content() + "]");
        }

        return doc;
    }

    private String cesify(String path, File theFile, boolean authenticated) throws IOException {
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
