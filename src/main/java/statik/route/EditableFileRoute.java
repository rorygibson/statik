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

    private static final String JQUERY_CSS = "<link href=\"" + PathsAndRoutes.STATIK_RESOURCES + "/jquery-ui/css/smoothness/jquery-ui-1.10.3.custom.min.css\" rel=\"stylesheet\" />";
    private static final String JQUERY_JS = "<script src=\"" + PathsAndRoutes.STATIK_RESOURCES + "/jquery-1.9.1.js\" type=\"text/javascript\"></script><script src=\"" + PathsAndRoutes.STATIK_RESOURCES + "/jquery-ui/js/jquery-ui-1.10.3.custom.min.js\" type=\"text/javascript\"></script>";
    private static final String AUTH_JS = "<script src=\"" + PathsAndRoutes.STATIK_RESOURCES + "/authenticated.js\" type=\"text/javascript\"></script><script src=\"" + PathsAndRoutes.STATIK_RESOURCES + "/authenticated-binding.js\" type=\"text/javascript\"></script><script src=\"" + PathsAndRoutes.STATIK_RESOURCES + "/dom.js\" type=\"text/javascript\"></script><script src=\"" + PathsAndRoutes.STATIK_RESOURCES + "/getpath.js\" type=\"text/javascript\"></script>";
    private static final String MENU_JS = "<script src=\"" + PathsAndRoutes.STATIK_RESOURCES + "/jquery.contextmenu.r2.packed.js\" type=\"text/javascript\"></script>";
    private static final String LOGOUT_BOX_HTML_TEMPLATE = "<div id=\"statik-auth-box\" style=\"position:absolute; top:20px; right:20px; border: solid lightgray 1px; background-color: lightgray; border-radius: 2px; padding: 5px\">" +
            "<a style=\"color: blue\" href=\"/statik/logout\">%s</a>" +
            "<br />" +
            "<a id=\"publish\" style=\"color: blue\" href=\"#\">%s</a>" +
            "</div>";
    private static final String EDITOR_HTML = "<div id=\"statik-editor-dialog\"></div>";
    private static final String MENU_HTML_TEMPLATE = "   <div style=\"display:none\" class=\"contextMenu\" id=\"editMenu\">\n" +
            "      <ul>\n" +
            "        <li id=\"edit\"> <span style=\"display:inline-block\" class=\"ui-icon ui-icon-pencil\"></span> %s </li>\n" +
            "        <li id=\"copy\"> <span style=\"display:inline-block\" class=\"ui-icon ui-icon-plusthick\"></span> %s </li>\n" +
            "      </ul>\n" +
            "    </div>";
    private static final String HTML_SUFFIX = ".html";
    private final SessionStore sessionStore;
    private final ContentStore contentStore;
    private final String fileBase;
    private String namedFile = null;
    private File fileNotFoundPage;
    private static final Logger LOG = LoggerFactory.getLogger(EditableFileRoute.class);


    public EditableFileRoute(ContentStore contentStore, String fileBase, String route, SessionStore sessionStore, String notFoundPage) {
        super(route);
        this.contentStore = contentStore;
        this.fileBase = fileBase;
        this.sessionStore = sessionStore;

        this.fileNotFoundPage = findRequestedFileFrom(notFoundPage);
    }

    public EditableFileRoute(ContentStore contentStore, String fileBase, String route, String namedFile, SessionStore sessionStore, String notFoundPage) {
        super(route);
        this.contentStore = contentStore;
        this.fileBase = fileBase;
        this.namedFile = namedFile;
        this.sessionStore = sessionStore;

        this.fileNotFoundPage = findRequestedFileFrom(notFoundPage);
    }

    @Override
    public Object handle(Request request, Response response) {
        String path = pathFrom(request);

        LOG.debug("GET " + path);

        File fileToServe = fileToServe(path);

        if (!fileToServe.exists()) {
            return do404(response, path);
        }

        try {
            response.status(200);
            return dataMatching(request, path, fileToServe);
        } catch (IOException e) {
            do404(response, path);
        }

        return Http.EMPTY_RESPONSE;
    }

    private String pathFrom(Request request) {
        HttpServletRequest httpReq = request.raw();
        return httpReq.getPathInfo() == null ? httpReq.getServletPath() : httpReq.getPathInfo();
    }

    private String dataMatching(Request request, String path, File fileToServe) throws IOException {
        if (mightContainCmsContent(fileToServe)) {
            LOG.debug("File is candidate for content editing");
            String fileContent;
            fileContent = FileUtils.readFileToString(fileToServe);

            return editableContentFor(path, fileContent, isAuthenticated(request));
        }
        return rawDataFrom(fileToServe);
    }

    private boolean isAuthenticated(Request request) {
        return sessionStore.hasSession(Http.sessionFrom(request));
    }

    private String rawDataFrom(File fileToServe) throws IOException {
        return FileUtils.readFileToString(fileToServe);
    }

    private Object do404(Response response, String missingFilePath) {
        LOG.error("Error reading file " + missingFilePath);
        response.status(404);

        try {
            writeFileToResponse(response, fileNotFoundPage);
        } catch (IOException e) {
            LOG.error("404 page HTML not found");
        }

        return Http.EMPTY_RESPONSE;
    }

    private File fileToServe(String path) {
        File theFile;
        if (thisRouteIsBoundToASpecificFile()) {
            theFile = findMySpecificFile();
        } else {
            theFile = findRequestedFileFrom(path);
        }
        return theFile;
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
        String publish = bundle.getString("authbox.publish");
        String edit = bundle.getString("editmenu.edit");
        String copy = bundle.getString("editmenu.copy");

        doc.head().append(JQUERY_CSS);
        doc.body().append(String.format(LOGOUT_BOX_HTML_TEMPLATE, logout, publish));
        doc.body().append(EDITOR_HTML);
        doc.body().append(String.format(MENU_HTML_TEMPLATE, edit, copy));
        doc.body().append(JQUERY_JS);
        doc.body().append(AUTH_JS);
        doc.body().append(MENU_JS);
        return doc;
    }

    private boolean mightContainCmsContent(File theFile) {
        return theFile.getName().endsWith(HTML_SUFFIX);
    }

    private Document replaceContent(Document doc, String path, boolean authenticated) {
        Map<String, ContentItem> contentItems = this.contentStore.findForPath(path);

        for (String selector : contentItems.keySet()) {
            ContentItem contentItem = contentItems.get(selector);

            if (!contentItem.live() && !authenticated) {
                continue;
            }

            replaceIndividualContentIem(doc, selector, contentItem);
        }

        return doc;
    }

    private void replaceIndividualContentIem(Document doc, String selector, ContentItem contentItem) {
        Element el = doc.select(selector).first();

        if (el != null) {
            el.html(contentItem.content());
            LOG.debug("Replaced element with selector [" + selector + "] with content [" + contentItem.content() + "]");
            return;
        }

        LOG.debug("Element doesn't exist; must be a copy. Creating and inserting");

        if (describesNthChild(selector)) {
            String siblingSelector = selector.substring(0, selector.lastIndexOf(':'));
            int lastChevron = selector.lastIndexOf('>');
            String tagName = selector.substring(lastChevron + 2).split(":")[0];

            LOG.debug("Creating element with sibling selector [" + siblingSelector + "], tagName [" + tagName + "]");
            el = doc.createElement(tagName);
            el.text(contentItem.content());

            Element sibling = doc.select(siblingSelector).last();
            sibling.after(el);
        } else {
            LOG.debug("Selector [" + selector + "] did not describe an element in a sequence");
        }
    }

    private boolean describesNthChild(String selector) {
        return selector.substring(0, selector.length() - 3).endsWith("nth-of-type");
    }

    private String editableContentFor(String path, String fileContent, boolean authenticated) {
        Document doc = Jsoup.parse(fileContent);
        doc = replaceContent(doc, path, authenticated);

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
