package statik.integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statik.util.Language;

import static org.junit.Assert.assertEquals;

public class I18nIT extends AbstractWebDriverIntTst {

    private static final Logger LOG = LoggerFactory.getLogger(I18nIT.class);

    @Before
    public void setUp() {
        driver.get(LOGOUT_PAGE);
        doLoginWith("admin", "password");
    }

    @Test
    public void switchToAnotherLanguage() {
        addTranslation(ONE_PARA_TEST_PAGE, By.tagName("p"), Language.Portuguese, "Statik páginas de teste");
        addTranslation(ONE_PARA_TEST_PAGE, By.tagName("p"), Language.French, "Le Statik test pages");

        String src = null;
        try {
            driver.get(ONE_PARA_TEST_PAGE + "?language=pt");
            src = driver.getPageSource();
            assertEquals("Should be the Portuguese content", "Statik páginas de teste", findEventually(By.tagName("p")).getText());
        } catch (Throwable t) {
            LOG.error("Timed out finding content.");
            LOG.error(src);
            throw t;
        }

        try {
            driver.get(ONE_PARA_TEST_PAGE + "?language=fr");
            src = driver.getPageSource();
            assertEquals("Should be the French content", "Le Statik test pages", findEventually(By.tagName("p")).getText());
        } catch (Throwable t) {
            LOG.error("Timed out finding content.");
            LOG.error(src);
            throw t;
        }
    }

    private void addTranslation(String path, By selector, Language language, String content) {
        driver.get(path);
        WebElement element = driver.findElement(selector);
        changeContentOf(element, content, language);
    }

}
