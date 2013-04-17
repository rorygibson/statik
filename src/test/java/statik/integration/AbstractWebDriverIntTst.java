package statik.integration;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.safari.SafariDriver;

public class AbstractWebDriverIntTst {

    protected static WebDriver driver;

    public static final String ROOT_PAGE = "http://localhost:8080/";
    public static final String LOGIN_ERROR_PAGE = "http://localhost:8080/login-error";
    public static final String LOGIN_PAGE = "http://localhost:8080/login";
    public static final String ONE_PARA_TEST_PAGE = "http://localhost:8080/one-para.html";
    public static final String TWO_PARA_TEST_PAGE = "http://localhost:8080/two-paras.html";


    @BeforeClass
    public static void setUp() {
        driver = new FirefoxDriver();
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }

    @After
    public void clearContentItemsCollection() {
        driver.get("http://localhost:8080/clear-db");
    }

    protected void sendLogin(String wrongUsername, String wrongPassword) {
        WebElement username = driver.findElement(By.name("username"));
        username.sendKeys(wrongUsername);
        WebElement password = driver.findElement(By.name("password"));
        password.sendKeys(wrongPassword);
        password.submit();
    }


    protected void doLoginWith(String username, String password) {
        driver.get(LOGIN_PAGE);
        sendLogin(username, password);
    }

}
