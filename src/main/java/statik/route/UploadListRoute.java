package statik.route;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadListRoute extends ResourceRoute {

    private static final Logger LOG = LoggerFactory.getLogger(UploadListRoute.class);
    private final File uploadDir;

    public UploadListRoute(String route, String uploadDirLocation) {
        super(route);
        this.uploadDir = new File(uploadDirLocation);
    }

    @Override
    public Object handle(Request request, Response response) {
        response.raw().setContentType("application/json");

        return doGet(request);
    }

    private Object doGet(Request request) {
        File[] files = this.uploadDir.listFiles();
        List<String> names = new ArrayList<String>();
        for (File f : files) {
            String name = f.getName();
            names.add(name);
        }
        return new JSONArray().toJSONString(names);
    }

}
