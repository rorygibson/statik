package statik.integration;

import org.junit.Test;
import org.openqa.selenium.By;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;

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
    public void loginWithNonexistantUser() {
        doLoginWith("dont-exist", "wrong-password");
        assertEquals("Should have been redirected to login error page", LOGIN_ERROR_PAGE, driver.getCurrentUrl());
    }


    @Test
    public void logout() {
        doLoginWith("rory", "password");
        driver.findElement(By.id("ces-auth-box"));
    }


}
