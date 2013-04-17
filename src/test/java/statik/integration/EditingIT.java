package statik.integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static junit.framework.Assert.assertEquals;

public class EditingIT extends AbstractWebDriverIntTst {


    @Before
    public void performLogin() {
        doLoginWith("rory", "password");
    }

    @Test
    public void simpleEdit() {
        driver.get(ONE_PARA_TEST_PAGE);
        changeContentOf("p", "new content");

        WebElement again = driver.findElement(By.cssSelector("p"));
        assertEquals("Text not as expected", "new content", again.getText());
    }


    @Test
    public void editOfSecondParaInASequence() {
        driver.get(TWO_PARA_TEST_PAGE);

        WebElement atFirst = driver.findElements(By.tagName("p")).get(1);
        assertEquals("Should be the second para", "two", atFirst.getText());

        changeContentOf(atFirst, "new content");

        WebElement afterEdit = driver.findElements(By.tagName("p")).get(1);
        assertEquals("Should now hve some different content", "new content", afterEdit.getText());
    }

    private void changeContentOf(WebElement el, String newContent) {
        el.click();
        el.clear();
        el.sendKeys(newContent);
        driver.findElement(By.tagName("body")).click();
    }

    private void changeContentOf(String tag, String newContent) {
        WebElement para = driver.findElement(By.cssSelector(tag));
        changeContentOf(para, newContent);
    }


}
