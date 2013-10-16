package statik.route;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UploadRoute extends ResourceRoute {

    private static final Logger LOG = LoggerFactory.getLogger(UploadRoute.class);
    private final File store;

    public UploadRoute(String route, String storageLocation) {
        super(route);
        this.store = new File(storageLocation);
    }

    @Override
    public Object handle(Request request, Response response) {
        response.raw().setContentType("text/html");
        return doPost(request);
    }

    private Object doPost(Request request) {
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request.raw());
            for (FileItem item : items) {
                if (item.isFormField()) {
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString();
                    LOG.debug("Found and ignored form field, name [" + fieldName + "], value [" + fieldValue + "]");
                } else {
                    String filename = FilenameUtils.getName(item.getName());
                    String pathname = this.store.getAbsolutePath() + "/" + filename;
                    LOG.debug("File upload, saving as " + pathname);
                    item.write(new File(pathname));
                }
            }
        } catch (Exception e) {
            LOG.error("Error parsing request", e.getMessage());
            return "ERROR";
        }

        return "OK";
    }

}
