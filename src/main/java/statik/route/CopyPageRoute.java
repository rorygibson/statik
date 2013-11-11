package statik.route;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import statik.util.Http;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public class CopyPageRoute extends Route {

    private static final Logger LOG = LoggerFactory.getLogger(CopyPageRoute.class);
    private String fileBase;

    public CopyPageRoute(String route, String fileBase) {
        super(route);
        this.fileBase = fileBase;
    }

    @Override
    public Object handle(Request request, Response response) {
        String domain = request.raw().getServerName();

        Map<String, String[]> parameterMap = request.raw().getParameterMap();
        String[] targetFilenames = parameterMap.get("targetFilename");
        String[] currentFilenames = parameterMap.get("currentFilename");

        if (targetFilenames.length == 0 || currentFilenames.length == 0) {
            LOG.error("Target or current filename was missing");
            return Http.FORBIDDEN;
        }

        String targetFilename = targetFilenames[0];
        String currentFilename = currentFilenames[0];

        LOG.info("Request to copy " + currentFilename + " to " + targetFilename);

        String absCurrentFilePath = this.fileBase + "/" + domain + "/" + currentFilename;
        String absTargetFilePath = this.fileBase + "/" + domain + "/" + targetFilename;

        LOG.info("Copying file-on-disk [" + absCurrentFilePath + "] to [" + absTargetFilePath + "]");

        File srcFile = new File(absCurrentFilePath);
        File targetFile = new File(absTargetFilePath);
        if (srcFile.exists()) {
            try {
                FileUtils.copyFile(srcFile, targetFile);
            } catch (IOException e) {
                LOG.error("Exception copying file: " + e.getMessage());
            }
        } else {
            LOG.error("Could not find current or destination File");
        }
        return Http.OK_RESPONSE;
    }
}

