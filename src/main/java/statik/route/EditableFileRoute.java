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


public class EditableFileRoute extends Route {

    private static final String JQUERY_CSS = "<link href=\"" + PathsAndRoutes.STATIK_RESOURCES + "/jquery-ui/css/smoothness/jquery-ui-1.10.3.custom.min.css\" rel=\"stylesheet\" />";
    private static final String JQUERY_JS = "<script src=\"" + PathsAndRoutes.STATIK_RESOURCES + "/jquery-1.9.1.js\" type=\"text/javascript\"></script><script src=\"" + PathsAndRoutes.STATIK_RESOURCES + "/jquery-ui/js/jquery-ui-1.10.3.custom.min.js\" type=\"text/javascript\"></script>";
    private static final String AUTH_JS = "<script src=\"" + PathsAndRoutes.STATIK_RESOURCES + "/authenticated-binding.js\" type=\"text/javascript\"></script>";

    private static final String HTML_SUFFIX = ".html";
    private final SessionStore sessionStore;
    private final ContentStore contentStore;
    private final String fileBase;
    private String namedFile = null;
    private static final Logger LOG = LoggerFactory.getLogger(EditableFileRoute.class);
    private String notFoundPageFilename;


    public EditableFileRoute(ContentStore contentStore, String fileBase, String route, SessionStore sessionStore, String notFoundPage) {
        super(route);
        this.contentStore = contentStore;
        this.fileBase = fileBase;
        this.sessionStore = sessionStore;
        this.notFoundPageFilename = notFoundPage;
    }

    public EditableFileRoute(ContentStore contentStore, String fileBase, String route, String namedFile, SessionStore sessionStore, String notFoundPage) {
        super(route);
        this.contentStore = contentStore;
        this.fileBase = fileBase;
        this.namedFile = namedFile;
        this.sessionStore = sessionStore;
        this.notFoundPageFilename = notFoundPage;
    }

    @Override
    public Object handle(Request request, Response response) {
        String path = pathFrom(request);
        String domain = domainFrom(request);

        LOG.debug("GET on [" + domain + "], path=" + path);

        File fileToServe = fileToServe(domain, path);

        if (!fileToServe.exists()) {
            return do404(response, path, findRequestedFileFrom(domain, this.notFoundPageFilename));
        }

        try {
            response.status(200);
            return dataMatching(domain, request, path, fileToServe);
        } catch (IOException e) {
            do404(response, path, findRequestedFileFrom(domain, this.notFoundPageFilename));
        }

        return Http.EMPTY_RESPONSE;
    }

    private String domainFrom(Request request) {
        return request.raw().getServerName();
    }

    private String pathFrom(Request request) {
        HttpServletRequest httpReq = request.raw();
        return httpReq.getPathInfo() == null ? httpReq.getServletPath() : httpReq.getPathInfo();
    }

    private String dataMatching(String domain, Request request, String path, File fileToServe) throws IOException {
        if (mightContainCmsContent(fileToServe)) {
            LOG.debug("File is candidate for content editing");
            String fileContent;
            fileContent = FileUtils.readFileToString(fileToServe);

            return editableContentFor(domain, path, fileContent, isAuthenticated(request));
        }
        return rawDataFrom(fileToServe);
    }

    private boolean isAuthenticated(Request request) {
        return sessionStore.hasSession(Http.sessionFrom(request));
    }

    private String rawDataFrom(File fileToServe) throws IOException {
        return FileUtils.readFileToString(fileToServe);
    }

    private Object do404(Response response, String missingFilePath, File fnfPage) {
        LOG.error("Error reading file " + missingFilePath);
        response.status(404);

        try {
            writeFileToResponse(response, fnfPage);
        } catch (IOException e) {
            LOG.error("404 page HTML not found");
        }

        return Http.EMPTY_RESPONSE;
    }

    private File fileToServe(String domain, String path) {
        File theFile;
        if (thisRouteIsBoundToASpecificFile()) {
            theFile = findMySpecificFile(domain);
        } else {
            theFile = findRequestedFileFrom(domain, path);
        }
        return theFile;
    }

    private File findMySpecificFile(String domain) {
        String path = fileBase + "/" + domain + "/" + this.namedFile;
        LOG.trace("Serving welcome file [" + path + "]");
        return new File(path);
    }

    private File findRequestedFileFrom(String domain, String path) {
        String fullPath = fileBase + "/" + domain + "/" + path;
        LOG.trace("Request for file, full path to file is [" + fullPath + "]");
        return new File(fullPath);
    }

    private boolean thisRouteIsBoundToASpecificFile() {
        return this.namedFile != null;
    }

    private Document makeEditable(Document doc) {
        doc.body().append(JQUERY_JS);
        doc.head().append(JQUERY_CSS);
        doc.body().append(AUTH_JS);
        return doc;
    }

    private boolean mightContainCmsContent(File theFile) {
        return theFile.getName().endsWith(HTML_SUFFIX);
    }

    private Document replaceContent(Document doc, String domain, String path, boolean authenticated) {
        Map<String, ContentItem> contentItems = this.contentStore.findForDomainAndPath(domain, path);

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

    private String editableContentFor(String domain, String path, String fileContent, boolean authenticated) {
        Document doc = Jsoup.parse(fileContent);
        doc = replaceContent(doc, domain, path, authenticated);

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
