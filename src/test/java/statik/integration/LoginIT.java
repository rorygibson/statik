package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static junit.framework.Assert.assertEquals;

public class LoginIT {

    @Test
    public void login() {
        WebDriver driver = new HtmlUnitDriver();
        driver.get("http://localhost:8080/login");

        WebElement username = driver.findElement(By.name("username"));
        username.sendKeys("fred");
        WebElement password = driver.findElement(By.name("password"));
        password.sendKeys("p4ssw0rd");
        password.submit();

        assertEquals("Should have been redirected to index page", "http://localhost:8080/", driver.getCurrentUrl());
    }

    @Test
    public void loginWithWrongPassword() {
        WebDriver driver = new HtmlUnitDriver();
        driver.get("http://localhost:8080/login");

        WebElement username = driver.findElement(By.name("username"));
        username.sendKeys("fred");
        WebElement password = driver.findElement(By.name("password"));
        password.sendKeys("wrong-password");
        password.submit();

        assertEquals("Should have been redirected to login error page", "http://localhost:8080/login-error", driver.getCurrentUrl());
    }


    @Test
    public void loginWithNonexistantUser() {
        WebDriver driver = new HtmlUnitDriver();
        driver.get("http://localhost:8080/login");

        WebElement username = driver.findElement(By.name("username"));
        username.sendKeys("dont-exist");
        WebElement password = driver.findElement(By.name("password"));
        password.sendKeys("wrong-password");
        password.submit();

        assertEquals("Should have been redirected to login error page", "http://localhost:8080/login-error", driver.getCurrentUrl());
    }

}
