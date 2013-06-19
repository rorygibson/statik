package statik.route;

import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.FileFilter;

public class ControlBoxRoute extends ThymeLeafResourceRoute {

    private static final String CONTROL_BOX_HTML = "control-box";
    private final String siteRoot;
    private final String[] siteNames;

    public ControlBoxRoute(String route, String siteRoot) {
        super(route);
        this.siteRoot = siteRoot;
        this.siteNames = siteNames();
    }

    @Override
    public Object handle(Request request, Response response) {
        Context ctx = new Context();
        String language = languageFrom(request);
        ctx.setVariable("sites", this.siteNames);
        ctx.setVariable("language", language);

        return processWithThymeLeaf(CONTROL_BOX_HTML, ctx);
    }

    private String[] siteNames() {
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
        return arr;
    }

}
