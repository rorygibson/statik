package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static junit.framework.Assert.assertEquals;

public class EditingIT extends AbstractWebDriverIntTst {

    @Test
    public void simpleEdit() {
        doLoginWith("rory", "password");

        driver.get(ONE_PARA_TEST_PAGE);
        WebElement para = driver.findElement(By.cssSelector("p"));
        assertEquals("Text not as expected", "content", para.getText());

        para.click();
        para.clear();
        para.sendKeys("new content");
        driver.findElement(By.tagName("body")).click();

        WebElement again = driver.findElement(By.cssSelector("p"));
        assertEquals("Text not as expected", "new content", again.getText());
    }


}
