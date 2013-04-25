package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class SessionPersistenceIT extends AbstractWebDriverIntTst {

    @Test
    public void ifYouHaveLoggedOutYouSeeTheLoginForm() {
        doLoginWith("rory", "password");
        driver.get(ONE_PARA_TEST_PAGE);
        driver.findElement(By.linkText("Log out")).click();
        driver.get(LOGIN_PAGE);
        WebElement h1 = driver.findElement(By.tagName("h1"));
        assertEquals("H1 had wrong text", "Sign in", h1.getText());
    }

    @Test
    public void onceYouHaveLoggedInYouDontSeeTheLoginForm() {
        doLoginWith("rory", "password");
        driver.get(LOGIN_PAGE);

        WebElement element = driver.findElement(By.className("error"));
        assertEquals("Wrong text", "You have already logged in.", element.getText());
    }

    @Test
    public void ifYouHaveNotLoggedInYouSeeTheLoginForm() {
        driver.get(LOGIN_PAGE);
        WebElement h1 = driver.findElement(By.tagName("h1"));
        assertEquals("H1 had wrong text", "Sign in", h1.getText());
    }
}
