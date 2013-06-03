package statik.integration;

import com.google.common.base.Function;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishingIT extends AbstractWebDriverIntTst {

    private static final Logger LOG = LoggerFactory.getLogger(PublishingIT.class);

    @Test
    public void unpublishedChangesAreNotVisibleUnlessLoggedIn() {
        doLoginWith("admin", "password");
        driver.get(ONE_PARA_TEST_PAGE);
        changeContentOf("p", "new content");

        doLogout();

        driver.get(ONE_PARA_TEST_PAGE);

        assertContentsOfPara("content");
    }

    @Test
    public void unpublishedChangesAreVisibleWhenLoggedIn() {
        doLoginWith("admin", "password");
        driver.get(ONE_PARA_TEST_PAGE);
        changeContentOf("p", "new content");

        // ... don't log out ...

        driver.get(ONE_PARA_TEST_PAGE);

        try {
            assertContentsOfPara("new content");
        } catch (Throwable t) {
            LOG.error("Timed out finding content.");
            LOG.error(driver.getPageSource());
            throw t;
        }
    }

    @Test
    public void publishedChangesAreVisibleWhenNotLoggedIn() {
        doLoginWith("admin", "password");
        driver.get(ONE_PARA_TEST_PAGE);
        changeContentOf("p", "new content");

        doPublish();
        doLogout();

        driver.get(ONE_PARA_TEST_PAGE);

        try {
            assertContentsOfPara("new content");
        } catch (Throwable t) {
            LOG.error("Timed out finding content.");
            LOG.error(driver.getPageSource());
            throw t;
        }

    }

    private void doPublish() {
        waitForPresenceOfItemById("control-box");
        driver.switchTo().frame("control-box");
        driver.findElement(By.id("publish")).click();
        driver.switchTo().defaultContent();
    }

    private void assertContentsOfPara(final String expectedContent) {
        WebDriverWait wait = new WebDriverWait(driver, 20l);
        wait.until(new Function<WebDriver, Object>() {
            @Override
            public Object apply(WebDriver driver) {
                WebElement section = driver.findElement(By.tagName("section"));
                WebElement para = section.findElement(By.tagName("p"));

                boolean equals = para.getText().equals(expectedContent);
                if (!equals) {
                    LOG.info("Polling for content change on [" + driver.getCurrentUrl() + "]; expect [" + expectedContent + "] but is [" + para.getText() + "]");
                }
                return equals;
            }

        });
    }
}
