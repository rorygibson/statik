package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static junit.framework.Assert.assertEquals;

public class CheckRunningIT {

    @Test
    public void checkItsRunning() {
        WebDriver driver = new HtmlUnitDriver();
        driver.get("http://localhost:8080/two-paras.html");
        WebElement para = driver.findElement(By.cssSelector("p"));
        assertEquals("Text not as expected", "one", para.getText());
    }

}
