package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;
import statik.util.Language;

import static org.junit.Assert.assertEquals;

public class I18nIT extends AbstractWebDriverIntTst {

    @Test
    public void switchToAnotherLanguage() {
        addTranslation(ONE_PARA_TEST_PAGE, By.tagName("p"), Language.Portuguese, "Statik páginas de teste");

        driver.get(ONE_PARA_TEST_PAGE);
        assertEquals("Should be the english title", "Statik test pages", driver.findElement(By.tagName("h1")).getText());

        driver.get(ONE_PARA_TEST_PAGE + "?lang=pt");
        assertEquals("Should be the Portuguese title", "Statik páginas de teste", driver.findElement(By.tagName("h1")).getText());
    }

    private void addTranslation(String path, By selector, Language language, String content) {
        doLoginWith("admin", "password");
        driver.get(path);


    }




}
