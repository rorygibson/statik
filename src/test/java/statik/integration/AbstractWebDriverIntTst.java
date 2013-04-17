package statik.integration;

import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class AbstractWebDriverIntTst {
    protected WebDriver driver;

    @Before
    public void setUp() {
        this.driver = driver();
    }

    protected void sendLogin(WebDriver driver, String wrongUsername, String wrongPassword) {
        WebElement username = driver.findElement(By.name("username"));
        username.sendKeys(wrongUsername);
        WebElement password = driver.findElement(By.name("password"));
        password.sendKeys(wrongPassword);
        password.submit();
    }

    protected WebDriver driver() {
        return this.driver == null ? new HtmlUnitDriver() : this.driver;
    }
}
