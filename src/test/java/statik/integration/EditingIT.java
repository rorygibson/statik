package statik.integration;

import com.google.common.base.Function;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static junit.framework.Assert.assertEquals;

public class EditingIT extends AbstractWebDriverIntTst {


    @Before
    public void performLogin() {
        doLoginWith("rory", "password");
    }


    @Test
    public void simpleEdit() throws InterruptedException {
        driver.get(ONE_PARA_TEST_PAGE);
        assertEquals("Text not as expected", "content", driver.findElement(By.tagName("p")).getText());

        changeContentOf("p", "new content");

        driver.get(ONE_PARA_TEST_PAGE);
        WebElement again = driver.findElement(By.cssSelector("p"));
        assertEquals("Text not as expected", "new content", again.getText());
    }


    @Test
    public void editOfSecondParaInASequence() {
        driver.get(TWO_PARA_TEST_PAGE);
        WebElement atFirst = driver.findElements(By.tagName("p")).get(1);
        assertEquals("Should be the second para", "two", atFirst.getText());

        changeContentOf(atFirst, "new content");

        driver.get(TWO_PARA_TEST_PAGE);
        WebElement afterEdit = driver.findElements(By.tagName("p")).get(1);
        assertEquals("Should now hve some different content", "new content", afterEdit.getText());
    }

    @Test
    public void editOfListItemInASequence() {
        driver.get(LIST_TEST_PAGE);
        WebElement atFirst = driver.findElements(By.tagName("li")).get(3);
        assertEquals("Should be the 4th item in the list", "four", atFirst.getText());

        changeContentOf(atFirst, "still the 4th, but different");

        driver.get(LIST_TEST_PAGE);
        WebElement afterEdit = driver.findElements(By.tagName("li")).get(3);
        assertEquals("Should now have some different content", "still the 4th, but different", afterEdit.getText());
    }


    private void changeContentOf(WebElement el, String newContent) {
        Actions a = new Actions(driver);
        a.doubleClick(el);
        a.perform();

        waitForPresenceOf("textarea");

        ((FirefoxDriver)driver).executeScript("document.getElementById('wysihtml5-textarea').value='" + newContent + "';");

        driver.findElement(By.tagName("form")).submit();
    }

    private void waitForPresenceOf(final String tagName) {
        WebDriverWait wait = new WebDriverWait(driver, 3);
        Object editor = wait.until(new Function<WebDriver, Object>() {
            @Override
            public Object apply(WebDriver webDriver) {
                return driver.findElement(By.tagName(tagName));
            }
        });
    }


    private void changeContentOf(String tag, String newContent) {
        WebElement para = driver.findElement(By.cssSelector(tag));
        changeContentOf(para, newContent);
    }
}
