package statik.integration.multiSite;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import statik.integration.AbstractWebDriverIntTst;

import static org.junit.Assert.assertEquals;

public class MultiSiteEditingIT extends AbstractWebDriverIntTst {


    public static final String SITE1_LOGIN_PAGE = "http://site1.localhost/statik-login";
    public static final String SITE2_LOGIN_PAGE = "http://site2.localhost/statik-login";
    private static String SITE1_PAGE = "http://site1.localhost/";
    private static String SITE2_PAGE = "http://site2.localhost/";

    @Test
    public void editSite1() throws InterruptedException {
        loginTo(SITE1_LOGIN_PAGE);

        driver.get(SITE1_PAGE);
        assertEquals("Text not as expected", "site1 content", driver.findElement(By.tagName("section")).getText());

        changeContentOf("section", "new site1 content");

        WebElement again = driver.findElement(By.cssSelector("section"));
        assertEquals("Text not as expected", "new site1 content", again.getText());
    }



    @Test
    public void editSite2() {
        loginTo(SITE2_LOGIN_PAGE);

        driver.get(SITE2_PAGE);
        assertEquals("Text not as expected", "site2 content", driver.findElement(By.tagName("section")).getText());

        changeContentOf("section", "new site2 content");

        WebElement again = driver.findElement(By.cssSelector("section"));
        assertEquals("Text not as expected", "new site2 content", again.getText());
    }


    @Test
    public void loginForOneSiteAllowsEditingOfBoth() {
        loginTo(SITE1_LOGIN_PAGE);
        selectToEditSite2();

        changeContentOf("section", "new site2 content");
        WebElement again = driver.findElement(By.cssSelector("section"));
        assertEquals("Text not as expected", "new site2 content", again.getText());
    }

    private void selectToEditSite2() {
        driver.switchTo().frame("control-box");
        driver.findElement(By.linkText("site2.localhost")).click();
        driver.switchTo().defaultContent();
    }


    private void loginTo(String loginPage) {
        driver.get(loginPage);
        sendLogin("admin", "password");
    }
}
