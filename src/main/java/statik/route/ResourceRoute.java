package statik.route;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import statik.content.LanguageFilter;
import statik.util.Http;
import statik.util.Language;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ResourceRoute extends Route {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceRoute.class);
    private static final int MAX_EXPIRY_IN_SECONDS = 300;

    public ResourceRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        String filename = request.splat()[0];
        LOG.trace("Request for file, path is [" + request.url() + "], file is [" + filename + "]");

        if (!testMode()) {
            setCacheable(response);
        }
        return writeClasspathFileToResponse(response, filename);
    }

    protected String languageFrom(Request request) {
        String cookie = request.cookie(LanguageFilter.LANGUAGE);
        Object attrib = request.raw().getAttribute(LanguageFilter.LANGUAGE);

        LOG.debug("Detected language [" + cookie + "] from request cookie");
        LOG.debug("Detected language [" + attrib + "] from request attribute");

        // request attrib will be more up-to-date; prefer it if present
        String language = null;
        if (attrib != null && !attrib.equals("")) {
            language = attrib.toString();
        } else if (cookie != null && cookie.equals("")) {
            language = cookie;
        }
        LOG.debug("Using language [" + language + "]");


        return StringUtils.isBlank(language) ? Language.Default.code() : language.toString();
    }

    protected boolean testMode() {
        return Boolean.getBoolean("testMode");
    }


    protected byte[] rawDataFrom(File fileToServe) throws IOException {
        return FileUtils.readFileToByteArray(fileToServe);
    }


    protected String pathFrom(Request request) {
        HttpServletRequest httpReq = request.raw();
        return httpReq.getPathInfo() == null ? httpReq.getServletPath() : httpReq.getPathInfo();
    }


    protected String domainFrom(Request request) {
        return request.raw().getServerName();
    }

    protected String contentTypeFrom(File file) {
        String type;
        String name = file.getName();
        String extension = name.substring(name.lastIndexOf('.') + 1);
        switch (extension) {
            case "html":
                type = "text/html";
                break;
            case "css":
                type = "text/css";
                break;
            case "js":
                type = "text/javascript";
                break;
            case "png":
                type = "image/png";
                break;
            case "woff":
                type = "application/x-font-woff";
                break;
            case "jpg":
            case "jpeg":
                type = "image/jpeg";
                break;
            default:
                type = "application/x-octet-stream";
        }
        return type;
    }


    protected static void setCacheable(Response r) {
        final Calendar inTwoMonths = new GregorianCalendar();
        inTwoMonths.setTime(new Date(System.currentTimeMillis()));
        inTwoMonths.add(Calendar.SECOND, MAX_EXPIRY_IN_SECONDS);
        r.raw().setDateHeader("Expires", inTwoMonths.getTimeInMillis());
        r.raw().setIntHeader("max-age", MAX_EXPIRY_IN_SECONDS);
        r.raw().setHeader("cache-control", "public");
    }

    protected Object writeClasspathFileToResponse(Response response, String filename) {
        String filePath = PathsAndRoutes.RESOURCE_ROOT_PATH + filename;
        LOG.debug("Writing classpath file " + filePath);
        String contentType = contentTypeFrom(new File(filePath));
        response.raw().setContentType(contentType);
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(filePath);
        try {
            if (resourceAsStream != null) {
                IOUtils.copy(resourceAsStream, response.raw().getOutputStream());
            } else {
                LOG.error("Error retrieving resource [" + filename + "]");
                response.status(404);
            }
        } catch (IOException e) {
            LOG.error("Error retrieving resource [" + filename + "]", e);
            response.status(404);
        }
        return Http.EMPTY_RESPONSE;
    }

    protected void writeFileToResponse(Response response, File theFile) throws IOException {
        LOG.debug("Writing file " + theFile.getAbsolutePath());
        if (theFile.getAbsolutePath().endsWith(".html")) {
            response.raw().setContentType("text/html");
        }
        byte[] bytes = FileUtils.readFileToByteArray(theFile);
        response.raw().getOutputStream().write(bytes);
    }


}
