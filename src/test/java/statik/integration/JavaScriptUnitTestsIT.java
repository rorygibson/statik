package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class JavaScriptUnitTestsIT extends AbstractWebDriverIntTst {

    @Test
    public void runQunitTests() {
        driver.get(QUNIT_TESTS_PAGE);
        List<WebElement> elements = driver.findElements(By.className("fail"));
        assertEquals("Found evidence of QUnit test failures", 0, elements.size());
    }

}
