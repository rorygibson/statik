package statik.route;

import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.FileFilter;

public class ControlBoxRoute extends ThymeLeafResourceRoute {

    private static final String CONTROL_BOX_HTML = "control-box";
    private final String siteRoot;

    public ControlBoxRoute(String route, String siteRoot) {
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

        String[] arr = new String[directories.length];
        int i = 0;
        for (File f : directories) {
            arr[i++] = f.getName();
        }

        Context ctx = new Context();
        ctx.setVariable("sites", arr);

        return processWithThymeLeaf(CONTROL_BOX_HTML, ctx);
    }

}
