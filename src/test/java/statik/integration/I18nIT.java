package statik.integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import statik.util.Language;

import static org.junit.Assert.assertEquals;

public class I18nIT extends AbstractWebDriverIntTst {

    @Before
    public void setUp() {
        driver.get(LOGOUT_PAGE);
        doLoginWith("admin", "password");
    }

    @Test
    public void switchToAnotherLanguage() {
        addTranslation(ONE_PARA_TEST_PAGE, By.tagName("p"), Language.Portuguese, "Statik páginas de teste");
        addTranslation(ONE_PARA_TEST_PAGE, By.tagName("p"), Language.French, "Le Statik test pages");

        driver.get(ONE_PARA_TEST_PAGE + "?language=pt");
        assertEquals("Should be the Portuguese content", "Statik páginas de teste", findEventually(By.tagName("p")).getText());

        driver.get(ONE_PARA_TEST_PAGE + "?language=fr");
        assertEquals("Should be the French content", "Le Statik test pages", findEventually(By.tagName("p")).getText());
    }

    private void addTranslation(String path, By selector, Language language, String content) {
        driver.get(path);
        WebElement element = driver.findElement(selector);
        changeContentOf(element, content, language);
    }

}
