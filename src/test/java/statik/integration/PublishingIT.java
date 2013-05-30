package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class PublishingIT extends AbstractWebDriverIntTst {


    @Test
    public void unpublishedChangesAreNotVisibleUnlessLoggedIn() {
        doLoginWith("admin", "password");
        driver.get(ONE_PARA_TEST_PAGE);
        changeContentOf("p", "new content");

        doLogout();

        driver.get(ONE_PARA_TEST_PAGE);

        WebElement again = driver.findElement(By.cssSelector("p"));
        assertEquals("Text should show the original, unedited content", "content", again.getText());
    }

    @Test
    public void unpublishedChangesAreVisibleWhenLoggedIn() {
        doLoginWith("admin", "password");
        driver.get(ONE_PARA_TEST_PAGE);
        changeContentOf("p", "new content");

        // ... don't log out ...

        driver.get(ONE_PARA_TEST_PAGE);
        WebElement again = driver.findElement(By.cssSelector("p"));
        assertEquals("Text should show the edited content", "new content", again.getText());
    }

    @Test
    public void publishedChangesAreVisibleWhenNotLoggedIn() {
        doLoginWith("admin", "password");
        driver.get(ONE_PARA_TEST_PAGE);
        changeContentOf("p", "new content");

        doPublish();
        doLogout();

        driver.get(ONE_PARA_TEST_PAGE);
        WebElement again = driver.findElement(By.cssSelector("p"));
        assertEquals("Text should show the edited content", "new content", again.getText());
    }

    private void doPublish() {
        driver.switchTo().frame("control-box");
        driver.findElement(By.id("publish")).click();
        driver.switchTo().defaultContent();
    }
}
