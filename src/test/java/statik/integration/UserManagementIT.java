package statik.integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserManagementIT extends AbstractWebDriverIntTst {

    private static final Logger LOG = LoggerFactory.getLogger(UserManagementIT.class);

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
        addUserWith("test@example.com", "password", "password");
        assertEquals("Should have been shown a success message", "Added the user", flashText());
    }

    @Test
    public void invalidUsername() {
        addUserWith("invalid", "password", "password");
        assertEquals("Should have been shown an error", "Email address must be valid", errorText());
    }

    @Test
    public void passwordsMustMatchWhenAddingUser() {
        addUserWith("test@example.com", "password", "doesnt-match");
        assertEquals("Should have been shown an error", "Passwords must match, must not be blank and must be 8+ characters long", errorText());
    }

    @Test
    public void deleteUser() {
        addUserWith("test@example.com", "password", "password");
        driver.get(USERS_PAGE);

        String src = driver.getPageSource();
        WebElement deleteLink = null;
        try {
            deleteLink = driver.findElement(By.className("btn-danger"));
        } catch (Throwable t) {
            LOG.error("Timed out finding delete link.");
            LOG.error(src);
            throw t;
        }

        deleteLink.click();

        try {
            assertEquals("Should have been shown a success message", "User deleted", flashText());
        } catch (Throwable t) {
            LOG.error("Timed out finding flash.");
            LOG.error(src);
            throw t;
        }
    }


    @Test
    public void changePassword() {
        addUserWith("test@example.com", "password", "password");
        driver.get(USERS_PAGE);
        changePasswordOf("test@example.com", "new-password");
        assertEquals("Should have been shown a success message", "User changed", flashText());
    }

    @Test
    public void changePasswordFailsWithPasswordProblem() {
        addUserWith("test@example.com", "password", "different");
        driver.get(USERS_PAGE);
        changePasswordOf("test@example.com", "new-password", "different");
        assertEquals("Should have been shown an error message", "Passwords must match, must not be blank and must be 8+ characters long", errorText());
    }

    @Test
    public void editUsername() {
        addUserWith("test@example.com", "password", "password");
        driver.get(USERS_PAGE);

        clickEditFor("test@example.com");

        WebElement username = findEventually(By.name("username"));
        assertEquals("Username field should hold value of current username", "test@example.com", username.getAttribute("value"));

        username.clear();
        username.sendKeys("new-user-name");
        username.submit();

        assertEquals("Should have been shown a success message", "User changed", flashText());
    }

    private void clickEditFor(String username) {
        List<WebElement> users = driver.findElements(By.tagName("li"));
        for (WebElement el : users) {
            if (el.getText().contains(username)) {
                WebElement edit = el.findElement(By.linkText("Edit"));
                edit.click();
            }
        }
    }

    private void addUserWith(String username, String password, String passwordAgain) {
        driver.get(USERS_PAGE);
        driver.findElement(By.linkText("Add a user")).click();

        findEventually(By.name("username")).sendKeys(username);
        findEventually(By.name("password")).sendKeys(password);
        findEventually(By.name("password-again")).sendKeys(passwordAgain);
        findEventually(By.name("username")).submit();
    }

    private void changePasswordOf(String username, String newPassword) {
        changePasswordOf(username, newPassword, newPassword);
    }

    private void changePasswordOf(String username, String newPassword, String newPasswordAgain) {
        clickEditFor(username);

        findEventually(By.name("password")).sendKeys(newPassword);
        findEventually(By.name("password-again")).sendKeys(newPasswordAgain);
        findEventually(By.name("password")).submit();
    }


    private String flashText() {
        WebElement flash = findEventually(By.className("alert-success"));
        return flash.getText();
    }

    private String errorText() {
        WebElement error = findEventually(By.className("alert-error"));
        return error.getText();
    }

}
