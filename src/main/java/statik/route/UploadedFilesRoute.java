package statik.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.IOException;

public class UploadedFilesRoute extends ResourceRoute {

    private static final Logger LOG = LoggerFactory.getLogger(UploadedFilesRoute.class);
    private final File store;

    public UploadedFilesRoute(String route, String storageLocation) {
        super(route);
        this.store = new File(storageLocation);
    }

    @Override
    public Object handle(Request request, Response response) {
        String path = pathFrom(request);
        String desired = store.getAbsolutePath() + "/" + new File(path).getName();
        File f = new File(desired);
        LOG.debug("Requested file " + desired);

        if (f.exists()) {
            LOG.debug("... exists");

            String contentType = contentTypeFrom(f);
            response.raw().setContentType(contentType);
            LOG.debug("Content type" + contentType);
            try {
                writeFileToResponse(response, f);
            } catch (IOException e) {
                LOG.error("Error writing file " + f.getAbsolutePath() + " to response:", e.getMessage());
            }
        }
        LOG.error("Requested file [" + desired + "] does not exist");
        return null;
    }

}
