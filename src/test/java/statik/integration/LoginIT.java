package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;

public class LoginIT extends AbstractWebDriverIntTst {

    @Test
    public void login() {
        driver.get("http://localhost:8080/login");
        sendLogin(driver, "fred", "p4ssw0rd");
        assertEquals("Should have been redirected to index page", "http://localhost:8080/", driver.getCurrentUrl());
    }


    @Test
    public void loginWithWrongPassword() {
        driver.get("http://localhost:8080/login");
        sendLogin(driver, "fred", "wrong-password");
        assertEquals("Should have been redirected to login error page", "http://localhost:8080/login-error", driver.getCurrentUrl());
    }


    @Test
    public void loginWithNonexistantUser() {
        driver.get("http://localhost:8080/login");
        sendLogin(driver(), "dont-exist", "wrong-password");
        assertEquals("Should have been redirected to login error page", "http://localhost:8080/login-error", driver.getCurrentUrl());
    }


    @Test
    public void logout() {
        driver.get("http://localhost:8080/login");
        sendLogin(driver, "rory", "password");
        driver.findElement(By.id("ces-auth-box"));
    }


}
