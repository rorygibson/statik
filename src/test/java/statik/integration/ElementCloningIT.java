package statik.integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ElementCloningIT extends AbstractWebDriverIntTst {

    @Before
    public void performLogin() {
        doLoginWith("rory", "password");
    }

    @Test
    public void contextMenuHas2EntriesWhenOnAList() {
        driver.get(LIST_TEST_PAGE);

        WebElement el = driver.findElement(By.tagName("li"));

        Actions a = new Actions(driver);
        a.contextClick(el);
        a.perform();

        WebElement menu = driver.findElement(By.id("jqContextMenu"));
        List<WebElement> lis = menu.findElements(By.tagName("li"));
        assertEquals("Should have 2 menu items", 2, lis.size());
    }

    @Test
    public void contextMenuHas1EntryWhenOnASingleLink() {
        driver.get(LINK_TEST_PAGE);

        WebElement el = driver.findElement(By.tagName("a"));

        Actions a = new Actions(driver);
        a.contextClick(el);
        a.perform();

        WebElement menu = driver.findElement(By.id("jqContextMenu"));
        WebElement copy = menu.findElement(By.id("copy"));

        assertEquals("Shouldn't be visible", "none", copy.getCssValue("display"));
    }


}
