package statik.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ResourceBundle;

public class InternationalisationReplacer {

    private static final String SELECTOR = "*[data-text-i18n], *[data-value-i18n]";
    private String sourceContent;

    private InternationalisationReplacer(String content) {
        this.sourceContent = content;
    }

    public static InternationalisationReplacer replace(String content) {
        return new InternationalisationReplacer(content);
    }

    public String with(ResourceBundle bundle) {
        Document document = Jsoup.parse(this.sourceContent);
        Elements elements = document.select(SELECTOR);

        for (Element el : elements) {
            // text nodes
            String key = el.attr("data-text-i18n");
            if (StringUtils.isNotBlank(key)) {
                String message = bundle.getString(key);
                el.text(message);
                el.removeAttr("data-text-i18n");
            }

            String key2 = el.attr("data-value-i18n");
            if (StringUtils.isNotBlank(key2)) {
                String message = bundle.getString(key2);
                el.attr("value", message);
                el.removeAttr("data-value-i18n");
            }
        }

        return document.toString();
    }
}
