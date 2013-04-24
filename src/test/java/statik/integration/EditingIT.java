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
    public void editLink() throws InterruptedException {
        driver.get(LINK_TEST_PAGE);

        WebElement originalLink = driver.findElement(By.tagName("a"));
        assertEquals("Link text wrong", "one para page", originalLink.getText());
        assertEquals("Link target wrong", "http://localhost:8080/one-para.html", originalLink.getAttribute("href"));

        changeContentOf("#first-link", "new link text");


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        WebElement changedLink = driver.findElement(By.id("first-link"));
        assertEquals("Link text wrong", "new link text", changedLink.getText());
        assertEquals("Link target wrong", "http://localhost:8080/one-para.html", changedLink.getAttribute("href"));
    }


    @Test
    public void editOfSecondParaInASequence() throws InterruptedException {
        driver.get(TWO_PARA_TEST_PAGE);
        WebElement atFirst = driver.findElements(By.tagName("p")).get(1);
        assertEquals("Should be the second para", "two", atFirst.getText());

        changeContentOf(atFirst, "new content");

        driver.get(TWO_PARA_TEST_PAGE);
        WebElement afterEdit = driver.findElements(By.tagName("p")).get(1);
        assertEquals("Should now hve some different content", "new content", afterEdit.getText());
    }

    @Test
    public void editOfListItemInASequence() throws InterruptedException {
        driver.get(LIST_TEST_PAGE);
        WebElement atFirst = driver.findElements(By.tagName("li")).get(3);
        assertEquals("Should be the 4th item in the list", "four", atFirst.getText());

        changeContentOf(atFirst, "still the 4th, but different");

        driver.get(LIST_TEST_PAGE);
        WebElement afterEdit = driver.findElements(By.tagName("li")).get(3);
        assertEquals("Should now have some different content", "still the 4th, but different", afterEdit.getText());
    }


    private void changeContentOf(WebElement el, String newContent) throws InterruptedException {
        Actions a = new Actions(driver);
        a.contextClick(el);
        a.perform();

        WebElement menu = driver.findElement(By.id("jqContextMenu"));
        menu.findElement(By.id("edit")).click();

        waitForPresenceOf("textarea");
        waitForPresenceOf("iframe");

        Thread.sleep(300);


        ((FirefoxDriver)driver).executeScript("document.editor.composer.setValue('" + newContent + "')");

        Thread.sleep(300);

        driver.findElement(By.tagName("form")).submit();
    }

    private void waitForPresenceOf(final String tagName) {
        WebDriverWait wait = new WebDriverWait(driver, 3);
        wait.until(new Function<WebDriver, Object>() {
            @Override
            public Object apply(WebDriver webDriver) {
                return driver.findElement(By.tagName(tagName));
            }
        });
    }

    private void waitUntilEditorIsActive() {
        WebDriverWait wait = new WebDriverWait(driver, 3);
        wait.until(new Function<WebDriver, Object>() {
            @Override
            public Object apply(WebDriver webDriver) {
                Object returned = ((FirefoxDriver)driver).executeScript("return document.editor == null");
                return returned.equals("false");
            }
        });
    }

    private void changeContentOf(String tag, String newContent) throws InterruptedException {
        WebElement para = driver.findElement(By.cssSelector(tag));
        changeContentOf(para, newContent);
    }
}
