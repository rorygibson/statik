package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static junit.framework.Assert.assertEquals;

public class LoginIT {

    @Test
    public void checkItsRunning() {
        WebDriver driver = new HtmlUnitDriver();
        driver.get("http://localhost:8080");
        WebElement para = driver.findElement(By.cssSelector("h1"));
        assertEquals("Text not as expected", "Welcome to the test pages", para.getText());
    }

}
