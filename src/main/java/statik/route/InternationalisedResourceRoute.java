package statik.route;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import statik.util.InternationalisationReplacer;

import java.util.ResourceBundle;

public class InternationalisedResourceRoute extends ResourceRoute {

    private static final Logger LOG = LoggerFactory.getLogger(InternationalisedResourceRoute.class);

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
        String content = fileAsString(filename);
        return InternationalisationReplacer.replace(content).with(ResourceBundle.getBundle("messages"));
    }

}
