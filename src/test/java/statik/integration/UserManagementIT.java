package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;

public class UserManagementIT extends AbstractWebDriverIntTst {

    @Test
    public void listsUsers() {
        driver.get(ADMIN_BASE_URL + "users");
        assertEquals("Wrong heading", "Users", driver.findElement(By.tagName("h1")).getText());

//        List<WebElement> elements = driver.findElements(By.tagName("li"));
//        assertEquals("There should be 2 users", 2, elements.size());
    }

}
