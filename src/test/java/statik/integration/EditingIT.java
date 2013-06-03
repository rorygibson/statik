package statik.integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EditingIT extends AbstractWebDriverIntTst {


    private static final Logger LOG = LoggerFactory.getLogger(EditingIT.class);

    @Before
    public void performLogin() {
        doLoginWith("admin", "password");
    }

    @Test
    public void simpleEdit() throws InterruptedException {
        driver.get(ONE_PARA_TEST_PAGE);
        assertEquals("Text not as expected", "content", driver.findElement(By.tagName("p")).getText());

        String src = null;
        try {
            src = driver.getPageSource();
            changeContentOf("p", "new content");
        } catch (Throwable t) {
            LOG.error("Timed out changing content.");
            LOG.error(src);
            throw t;
        }

        try {
            src = driver.getPageSource();
            WebElement again = findEventually(By.tagName("p"));
            assertEquals("Text not as expected", "new content", again.getText());
        } catch (Throwable t) {
            LOG.error("Timed out finding content.");
            LOG.error(src);
            throw t;
        }
    }

    @Test
    public void editLink() throws InterruptedException {
        driver.get(LINK_TEST_PAGE);

        WebElement originalLink = driver.findElement(By.id("first-link"));
        assertEquals("Link text wrong", "one para page", originalLink.getText());
        assertTrue("Link target wrong", originalLink.getAttribute("href").endsWith("one-para.html"));

        String src = null;
        try {
            changeContentOf("#first-link", "new link text");

            src = driver.getPageSource();
            WebElement changedLink = driver.findElement(By.id("first-link"));
            assertEquals("Link text wrong", "new link text", changedLink.getText());
            assertTrue("Link target wrong", changedLink.getAttribute("href").endsWith("one-para.html"));
        } catch (Throwable t) {
            LOG.error("Timed out finding content.");
            LOG.error(src);
            throw t;
        }
    }


    @Test
    public void editOfSecondParaInASequence() throws InterruptedException {
        driver.get(TWO_PARA_TEST_PAGE);
        WebElement secondPara = driver.findElements(By.tagName("p")).get(1);
        assertEquals("Should be the second para", "two", secondPara.getText());

        changeContentOf(secondPara, "new content");

        String src = null;
        try {
            driver.get(TWO_PARA_TEST_PAGE);
            src = driver.getPageSource();
            WebElement section = driver.findElement(By.tagName("section"));
            List<WebElement> paras = section.findElements(By.tagName("p"));
            assertEquals("Should still have 2 paras", 2, paras.size());
            WebElement secondParaAfterEdit = paras.get(1);
            assertEquals("Should now have some different content", "new content", secondParaAfterEdit.getText());
        } catch (Throwable t) {
            LOG.error("Timed out finding content.");
            LOG.error(src);
            throw t;
        }
    }

    @Test
    public void editOfListItemInASequence() throws InterruptedException {
        driver.get(LIST_TEST_PAGE);
        WebElement atFirst = driver.findElements(By.tagName("li")).get(3);
        assertEquals("Should be the 4th item in the list", "four", atFirst.getText());

        changeContentOf(atFirst, "still the 4th, but different");

        driver.get(LIST_TEST_PAGE);
        String src = driver.getPageSource();
        try {
            WebElement afterEdit = driver.findElements(By.tagName("li")).get(3);
            assertEquals("Should now have some different content", "still the 4th, but different", afterEdit.getText());
        } catch (Throwable t) {
            LOG.error("Timed out finding content.");
            LOG.error(src);
            throw t;
        }
    }

    @Test
    public void copyAndEditParagraph() throws InterruptedException {
        driver.get(ONE_PARA_TEST_PAGE);
        WebElement original = driver.findElement(By.tagName("p"));

        copy(original);
        WebElement theCopy = driver.findElements(By.tagName("p")).get(1);
        changeContentOf(theCopy, "copied");
        String src = null;
        try {
            driver.get(ONE_PARA_TEST_PAGE);
            src = driver.getPageSource();
            List<WebElement> afterEdit = driver.findElements(By.tagName("p"));
            assertEquals("Should have two paras", 2, afterEdit.size());
            assertEquals("Original element text", "content", afterEdit.get(0).getText());
            assertEquals("Copied element text", "copied", afterEdit.get(1).getText());
        } catch (Throwable t) {
            LOG.error("Timed out finding content.");
            LOG.error(src);
            throw t;
        }
    }

}
