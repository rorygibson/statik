package statik.route;

import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import statik.util.InternationalisationReplacer;

import java.util.ResourceBundle;

public class InternationalisedResourceRoute extends ResourceRoute {


    private static final Logger LOG = Logger.getLogger(InternationalisedResourceRoute.class);

    public InternationalisedResourceRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        String filename = request.splat()[0];
        LOG.trace("Request for i18n file, path is [" + request.url() + "], file is [" + filename + "]");

        return i18n(filename);
    }

    protected String i18n(String filename) {
        InternationalisationReplacer replacer = new InternationalisationReplacer();
        String content = fileAsString(filename);
        return replacer.replace(content).with(ResourceBundle.getBundle("messages"));
    }

}
