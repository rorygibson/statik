package statik.integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EditingIT extends AbstractWebDriverIntTst {

    public static final int PERIOD_TO_WAIT_FOR_EDITOR = 500;
    public static final int PERIOD_TO_WAIT_FOR_CHANGES = 500;

    @Before
    public void performLogin() {
        doLoginWith("rory", "password");
    }

    @Test
    public void simpleEdit() throws InterruptedException {
        driver.get(ONE_PARA_TEST_PAGE);
        assertEquals("Text not as expected", "content", driver.findElement(By.tagName("p")).getText());

        changeContentOf("p", "new content");

        WebElement again = driver.findElement(By.cssSelector("p"));
        assertEquals("Text not as expected", "new content", again.getText());
    }

    @Test
    public void editLink() throws InterruptedException {
        driver.get(LINK_TEST_PAGE);

        WebElement originalLink = driver.findElement(By.id("first-link"));
        assertEquals("Link text wrong", "one para page", originalLink.getText());
        assertTrue("Link target wrong", originalLink.getAttribute("href").endsWith("one-para.html"));

        changeContentOf("#first-link", "new link text");

        WebElement changedLink = driver.findElement(By.id("first-link"));
        assertEquals("Link text wrong", "new link text", changedLink.getText());
        assertTrue("Link target wrong", changedLink.getAttribute("href").endsWith("one-para.html"));
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

    @Test
    public void copyAndEditParagraph() throws InterruptedException {
        driver.get(ONE_PARA_TEST_PAGE);
        WebElement original = driver.findElement(By.tagName("p"));

        copy(original);
        WebElement theCopy = driver.findElements(By.tagName("p")).get(1);
        changeContentOf(theCopy, "copied");

        driver.get(ONE_PARA_TEST_PAGE);
        List<WebElement> afterEdit = driver.findElements(By.tagName("p"));
        assertEquals("Should have two paras", 2, afterEdit.size());
        assertEquals("Original element text", "content", afterEdit.get(0).getText());
        assertEquals("Copied element text", "copied", afterEdit.get(1).getText());
    }

    private void copy(WebElement el) {
        Actions a = new Actions(driver);
        a.contextClick(el);
        a.perform();

        WebElement menu = driver.findElement(By.id("jqContextMenu"));
        menu.findElement(By.id("copy")).click();
    }

    private void sleepForMs(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            //
        }
    }

    private void changeContentOf(WebElement el, String newContent) {
        Actions a = new Actions(driver);
        a.contextClick(el);
        a.perform();

        WebElement menu = driver.findElement(By.id("jqContextMenu"));
        menu.findElement(By.id("edit")).click();

        waitForPresenceOf("iframe");

        sleepForMs(PERIOD_TO_WAIT_FOR_EDITOR);
        ((FirefoxDriver)driver).executeScript("document.editor.composer.setValue('" + newContent + "')");
        sleepForMs(PERIOD_TO_WAIT_FOR_CHANGES);
        driver.findElement(By.id("submit")).click();
        sleepForMs(PERIOD_TO_WAIT_FOR_CHANGES);
    }

    private void changeContentOf(String tag, String newContent) {
        WebElement para = driver.findElement(By.cssSelector(tag));
        changeContentOf(para, newContent);
    }
}
