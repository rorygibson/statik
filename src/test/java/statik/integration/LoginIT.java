package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;


public class LoginIT extends AbstractWebDriverIntTst {


    @Test
    public void login() {
        doLoginWith("fred", "p4ssw0rd");
        assertEquals("Should have been redirected to the login page", "Welcome to the test pages", driver.findElement(By.tagName("h1")).getText());
    }


    @Test
    public void loginWithWrongPassword() {
        doLoginWith("fred", "wrong-password");
        assertEquals("Should have been redirected to the login error page", "Sign in - error", driver.findElement(By.tagName("h1")).getText());
    }


    @Test
    public void loginWithNonexistentUser() {
        doLoginWith("dont-exist", "wrong-password");
        assertEquals("Should have been redirected to the login error page", "Sign in - error", driver.findElement(By.tagName("h1")).getText());
    }


    @Test
    public void logout() {
        doLoginWith("rory", "password");
        driver.findElement(By.id("statik-auth-box"));
    }


}
