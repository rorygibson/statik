package statik.route;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import spark.Request;
import spark.Response;

public abstract class ThymeLeafResourceRoute extends ResourceRoute {

    private static final Logger LOG = LoggerFactory.getLogger(ThymeLeafResourceRoute.class);

    public ThymeLeafResourceRoute(String route) {
        super(route);
    }

    @Override
    public Object handle(Request request, Response response) {
        String templateName = resolveTemplateName(request);
        return processWithThymeLeaf(templateName);
    }

    protected String processWithThymeLeaf(String templateName) {
        TemplateEngine engine = templateEngine();
        Context ctx = new Context();
        return engine.process(templateName, ctx);
    }

    protected TemplateEngine templateEngine() {
        ClassLoaderTemplateResolver tr = new ClassLoaderTemplateResolver();
        tr.setTemplateMode("HTML5");
        tr.setSuffix(".html");
        tr.setPrefix("statik-resources/");

        IMessageResolver messageResolver = new StandardMessageResolver();
        TemplateEngine engine = new TemplateEngine();
        engine.setMessageResolver(messageResolver);
        engine.setTemplateResolver(tr);
        return engine;
    }

    protected String resolveTemplateName(Request request) {
        String filename = request.splat()[0];
        LOG.trace("Request for i18n file, path is [" + request.url() + "], file is [" + filename + "]");
        return filename;
    }
}
