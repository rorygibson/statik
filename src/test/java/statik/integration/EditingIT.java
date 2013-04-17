package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static junit.framework.Assert.assertEquals;

public class EditingIT extends AbstractWebDriverIntTst {


    @Test
    public void checkItsRunning() {
        driver.get("http://localhost:8080/two-paras.html");
        WebElement para = driver.findElement(By.cssSelector("p"));
        assertEquals("Text not as expected", "one", para.getText());
    }
    

}
