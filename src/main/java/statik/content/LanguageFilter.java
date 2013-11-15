package statik.content;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;
import spark.Request;
import spark.Response;
import statik.Notifier;
import statik.util.Language;

import javax.servlet.http.Cookie;

public class LanguageFilter extends Filter {

    public static final String LANGUAGE = "language";
    private static final Logger LOG = LoggerFactory.getLogger(LanguageFilter.class);
    private final Notifier notifier;

    public LanguageFilter(String route, Notifier notifier) {
        super(route);
        this.notifier = notifier;
    }

    @Override
    public void handle(Request request, Response response) {
        String currentLang = request.cookie(LANGUAGE);
        String targetLang = request.queryParams(LANGUAGE);

        if (targetLang != null && !targetLang.equals(currentLang)) {
            LOG.trace("Changing language. Target language [" + targetLang + "]");
            this.notifier.notify("language-changed", targetLang , request.raw().getServerName());

            Cookie removeCookie = new Cookie(LANGUAGE, "");
            removeCookie.setPath("/");
            removeCookie.setMaxAge(0);

            Cookie newCookie = new Cookie(LANGUAGE, targetLang);
            removeCookie.setPath("/");
            removeCookie.setMaxAge(-1); // forever

            response.raw().addCookie(removeCookie);
            response.raw().addCookie(newCookie);

            currentLang = targetLang;

        }

        request.raw().setAttribute(LANGUAGE, currentLang);
    }


}
