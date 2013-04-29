package statik.route;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import statik.content.ContentItem;
import statik.content.ContentStore;
import statik.session.SessionStore;
import statik.util.Http;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;


public class EditableFileRoute extends Route {

    private static final String JQUERY_CSS = "<link href=\"/statik-resources/jquery-ui/css/jquery-ui-1.10.2.custom.min.css\" rel=\"stylesheet\" />";
    private static final String JQUERY_JS = "<script src=\"/statik-resources/jquery-1.9.1.js\" type=\"text/javascript\"></script><script src=\"/statik-resources/jquery-ui/js/jquery-ui-1.10.2.custom.min.js\" type=\"text/javascript\"></script>";
    private static final String AUTH_JS = "<script src=\"/statik-resources/authenticated.js\" type=\"text/javascript\"></script><script src=\"/statik-resources/authenticated-binding.js\" type=\"text/javascript\"></script><script src=\"statik-resources/dom.js\" type=\"text/javascript\"></script><script src=\"/statik-resources/getpath.js\" type=\"text/javascript\"></script>";
    private static final String MENU_JS = "<script src=\"/statik-resources/jquery.contextmenu.r2.packed.js\" type=\"text/javascript\"></script>";
    private static final String LOGOUT_BOX_HTML_TEMPLATE = "<div id=\"statik-auth-box\" style=\"position:absolute; top:20px; right:20px; border: solid lightgray 1px; background-color: lightgray; border-radius: 4px; padding: 5px\"><a style=\"color: blue\" href=\"/logout\">%s</a></div>";
    private static final String EDITOR_HTML = "<div id=\"statik-editor-dialog\"></div>";
    private static final String MENU_HTML_TEMPLATE = "   <div style=\"display:none\" class=\"contextMenu\" id=\"editMenu\">\n" +
            "      <ul>\n" +
            "        <li id=\"edit\"><img src=\"/statik-resources/edit.png\" /> %s </li>\n" +
            "      </ul>\n" +
            "    </div>";
    private static String notFoundPage = "";
    private static final String HTML_SUFFIX = ".html";
    private final SessionStore sessionStore;
    private final ContentStore contentStore;
    private final String fileBase;
    private String namedFile = null;
    private static final Logger LOG = LoggerFactory.getLogger(EditableFileRoute.class);

    public EditableFileRoute(ContentStore contentStore, String fileBase, String route, SessionStore sessionStore, String notFoundPage) {
        super(route);
        this.contentStore = contentStore;
        this.fileBase = fileBase;
        this.sessionStore = sessionStore;
        this.notFoundPage = notFoundPage;
    }

    public EditableFileRoute(ContentStore contentStore, String fileBase, String route, String namedFile, SessionStore sessionStore, String notFoundPage) {
        super(route);
        this.contentStore = contentStore;
        this.fileBase = fileBase;
        this.namedFile = namedFile;
        this.sessionStore = sessionStore;
        this.notFoundPage = notFoundPage;
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

        try {
            if (!theFile.exists()) {
                LOG.warn("File not found [" + theFile.getAbsolutePath() + "]");
                File the404File = findRequestedFileFrom(notFoundPage);
                writeFileToResponse(response, the404File);
                response.status(404);
                return Http.EMPTY_RESPONSE;
            }

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
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        String logout = bundle.getString("authbox.logout");
        String edit = bundle.getString("editmenu.edit");

        doc.head().append(JQUERY_CSS);
        doc.body().append(String.format(LOGOUT_BOX_HTML_TEMPLATE, logout));
        doc.body().append(EDITOR_HTML);
        doc.body().append(String.format(MENU_HTML_TEMPLATE, edit));
        doc.body().append(JQUERY_JS);
        doc.body().append(AUTH_JS);
        doc.body().append(MENU_JS);
        return doc;
    }

    private boolean mightContainCmsContent(File theFile) {
        return theFile.getName().endsWith(HTML_SUFFIX);
    }

    private Document replaceContent(Document doc, String path) {
        Map<String, ContentItem> contentItems = this.contentStore.findForPath(path);

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
