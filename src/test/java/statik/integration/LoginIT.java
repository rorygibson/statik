package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;


public class LoginIT extends AbstractWebDriverIntTst {


    @Test
    public void login() {
        doLoginWith("fred", "p4ssw0rd");
        assertEquals("Should have been redirected to index page", ROOT_PAGE, driver.getCurrentUrl());
    }


    @Test
    public void loginWithWrongPassword() {
        doLoginWith("fred", "wrong-password");
        assertEquals("Should have been redirected to login error page", LOGIN_ERROR_PAGE, driver.getCurrentUrl());
    }


    @Test
    public void loginWithNonexistentUser() {
        doLoginWith("dont-exist", "wrong-password");
        assertEquals("Should have been redirected to login error page", LOGIN_ERROR_PAGE, driver.getCurrentUrl());
    }


    @Test
    public void logout() {
        doLoginWith("rory", "password");
        driver.findElement(By.id("ces-auth-box"));
    }


}
