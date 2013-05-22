package statik.integration;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserManagementIT extends AbstractWebDriverIntTst {

    @Before
    public void performLogin() {
        doLoginWith("admin", "password");
    }


    @Test
    public void usersPageIsSecured() {
        driver.get(LOGOUT_PAGE);

        driver.get(USERS_PAGE);
        assertEquals("Should see the login page", "Sign in", driver.findElement(By.tagName("h1")).getText());
    }

    @Test
    public void listsUsers() {
        driver.get(USERS_PAGE);
        assertEquals("Wrong heading", "Users", driver.findElement(By.tagName("h1")).getText());

        List<WebElement> elements = driver.findElements(By.tagName("li"));
        assertEquals("There should be 1 users", 1, elements.size());
    }

    @Test
    public void addUser() {
        addUserWith("tester", "password", "password");
        assertEquals("Should have been shown a success message", "Added the user", driver.findElement(By.className("flash")).getText());
    }

    @Test
    public void passwordsMustMatchWhenAddingUser() {
        addUserWith("tester", "password", "doesnt-match");
        assertEquals("Should have been shown an error", "Passwords must match and must not be blank", driver.findElement(By.className("error")).getText());
    }

    @Test
    public void deleteUser() {
        addUserWith("tester", "password", "password");
        driver.get(USERS_PAGE);
        List<WebElement> users = driver.findElements(By.tagName("li"));
        int startingSize = users.size();

        for (WebElement el : users) {
            if (el.getText().contains("tester")) {
                el.findElement(By.linkText("Delete")).click();
                WebElement flash = driver.findElement(By.className("flash"));
                assertEquals("Should have been shown a success message", "User deleted", flash.getText());
            }
        }
        assertEquals("Should have deleted one user", startingSize - 1, driver.findElements(By.tagName("li")).size());
    }

    @Test @Ignore
    public void editUser() {
        fail("todo");
    }

    private void addUserWith(String username, String password, String passwordAgain) {
        driver.get(USERS_PAGE);
        driver.findElement(By.linkText("Add a user")).click();
        driver.findElement(By.name("password-again")).sendKeys(passwordAgain);
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("username")).submit();
    }

}
