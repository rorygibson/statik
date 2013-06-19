package statik.integration;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static org.junit.Assert.assertEquals;

public class ElementCloningIT extends AbstractWebDriverIntTst {

    @Before
    public void performLogin() {
        doLoginWith("admin", "password");
    }

    @Test
    public void contextMenuHas2EntriesWhenOnAList() {
        driver.get(LIST_TEST_PAGE);

        WebElement el = driver.findElement(By.tagName("li"));

        Actions a = new Actions(driver);
        a.contextClick(el);
        a.perform();

        WebElement menu = driver.findElement(By.id("jqContextMenu"));
        menu.findElement(By.id("edit"));
        menu.findElement(By.id("copy"));
    }

    @Test      @Ignore
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
