package statik.util;

import org.junit.Test;

import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;

public class InternationalisationReplacerTest {


    @Test
    public void replacesI18nText() {
        InternationalisationReplacer replacer = new InternationalisationReplacer();
        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        String content = "<html><body><h1 data-text-i18n=\"login.heading\">original text</h1></body></html>";
        String actualContent = replacer.replace(content).with(bundle);
        String expectedContent = "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  <h1>Sign in</h1>\n" +
                " </body>\n" +
                "</html>";

        assertEquals("Incorrectly replaced", expectedContent, actualContent);
    }


    @Test
    public void replacesI18nValue() {
        InternationalisationReplacer replacer = new InternationalisationReplacer();
        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        String content = "<html><body><input type=\"submit\" data-value-i18n=\"login.submit\" value=\"original value\" /></body></html>";
        String actualContent = replacer.replace(content).with(bundle);
        String expectedContent = "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  <input type=\"submit\" value=\"Log in\" />\n" +
                " </body>\n" +
                "</html>";

        assertEquals("Incorrectly replaced", expectedContent, actualContent);
    }

}
