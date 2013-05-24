package statik.route;

import org.json.simple.JSONArray;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.File;
import java.io.FileFilter;

public class ListSitesRoute extends Route {

    private final String siteRoot;

    public ListSitesRoute(String route, String siteRoot) {
        super(route);
        this.siteRoot = siteRoot;
    }

    @Override
    public Object handle(Request request, Response response) {
        File root = new File(siteRoot);
        File[] directories = root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        JSONArray arr = new JSONArray();
        for (File f : directories) {
            arr.add(f.getName());
        }

        response.raw().setContentType("application/json");
        return arr.toJSONString();
    }

}
