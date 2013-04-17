package statik.integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;

public class LoginIT {

    private WebDriver driver;

    @Before
    public void setUp() {
        this.driver = driver();
    }

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


    private void sendLogin(WebDriver driver, String wrongUsername, String wrongPassword) {
        WebElement username = driver.findElement(By.name("username"));
        username.sendKeys(wrongUsername);
        WebElement password = driver.findElement(By.name("password"));
        password.sendKeys(wrongPassword);
        password.submit();
    }


    private WebDriver driver() {
        return this.driver == null ? new HtmlUnitDriver() : this.driver;
    }
}
