package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;

public class FileNotFoundIT extends AbstractWebDriverIntTst {

    @Test
    public void get404ForFNF() {
        driver.get(BASE_URL + "/i-dont-exist");
        assertEquals("Heading was wrong", "404 :: localhost :: page not found", driver.findElement(By.tagName("h1")).getText());
    }

}
