package statik.content;

import org.apache.commons.lang3.StringUtils;
import spark.Filter;
import spark.Request;
import spark.Response;

import javax.servlet.http.Cookie;

public class LanguageFilter extends Filter {

    public static final String LANGUAGE = "language";

    public LanguageFilter(String route) {
        super(route);
    }

    @Override
    public void handle(Request request, Response response) {
        String requestedLanguage = request.queryParams(LANGUAGE);
        String cookieLanguage = request.cookie(LANGUAGE);

        String desiredLanguage = null;
        if (StringUtils.isNotBlank(requestedLanguage)) {
            desiredLanguage = requestedLanguage;
        } else if ( StringUtils.isNotBlank(cookieLanguage)){
            desiredLanguage = cookieLanguage;
        }

        if (desiredLanguage != null) {
            response.removeCookie(LANGUAGE);
            response.raw().addCookie(new Cookie(LANGUAGE, desiredLanguage));
            request.raw().setAttribute(LANGUAGE, desiredLanguage);
        }
    }


}
