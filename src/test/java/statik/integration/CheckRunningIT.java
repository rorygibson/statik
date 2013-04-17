package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static junit.framework.Assert.assertEquals;

public class CheckRunningIT extends AbstractWebDriverIntTst {

    @Test
    public void checkItsRunning() {
        driver.get(TWO_PARA_TEST_PAGE);
        WebElement para = driver.findElement(By.cssSelector("p"));
        assertEquals("Text not as expected", "one", para.getText());
    }

}
