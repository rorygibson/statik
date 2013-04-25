package statik.content;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class JsoupTest {

    @Test
    public void learning() {
        String start = "<html><body><p></p></body></html>";
        Document document = Jsoup.parse(start);
        Elements paras = document.select("p");
        Element para = paras.get(0);

        para.html("<b>some html</b>");
        System.out.println(document.toString());

    }
}
