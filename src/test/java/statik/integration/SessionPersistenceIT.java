package statik.integration;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import statik.util.Http;

import static org.junit.Assert.assertEquals;

public class SessionPersistenceIT extends AbstractWebDriverIntTst {

    @After
    public void tearDown() {
        driver.get(LOGOUT_PAGE);
    }

    @Test
    public void ifYouHaveLoggedOutYouSeeTheLoginForm() {
        doLoginWith("admin", "password");
        tearDown();

        driver.get(LOGIN_PAGE);
        WebElement h1 = driver.findElement(By.tagName("h1"));
        assertEquals("H1 had wrong text", "Sign in", h1.getText());
    }

    @Test
    public void onceYouHaveLoggedInYouDontSeeTheLoginForm() {
        doLoginWith("admin", "password");

        driver.get(LOGIN_PAGE);
        WebElement element = driver.findElement(By.className("alert-info"));
        assertEquals("Wrong text", "You have already logged in.", element.getText());
    }

    @Test
    public void ifYouHaveNotLoggedInYouSeeTheLoginForm() {
        driver.get(LOGIN_PAGE);
        WebElement h1 = driver.findElement(By.tagName("h1"));
        assertEquals("H1 had wrong text", "Sign in", h1.getText());
    }

    @Test
    public void editingSessionsPersistAcrossBrowserSessions() {
        driver.manage().deleteAllCookies();

        doLoginWith("admin", "password");
        Cookie cookie = driver.manage().getCookieNamed(Http.COOKIE_NAME);
        driver.quit();

        driver = runningDriver();
        driver.get(ROOT_PAGE); // WebDriver won't let you set a cookie while you're on about:blank - have to visit a page on the domain first
        driver.manage().addCookie(cookie);

        driver.get(LOGIN_PAGE);
        WebElement element = driver.findElement(By.className("alert-info"));
        assertEquals("Wrong text", "You have already logged in.", element.getText());
    }
}
