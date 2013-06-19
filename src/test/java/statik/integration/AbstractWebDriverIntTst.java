package statik.integration;

import com.google.common.base.Function;
import org.junit.After;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statik.route.PathsAndRoutes;
import statik.util.Language;

import static org.junit.Assert.assertEquals;

public class AbstractWebDriverIntTst {

    protected static WebDriver driver;

    public static String BASE_URL;

    public static String ROOT_PAGE;
    public static String LOGIN_ERROR_PAGE;
    public static String LOGIN_PAGE;
    public static String ONE_PARA_TEST_PAGE;
    public static String TWO_PARA_TEST_PAGE;
    public static String LIST_TEST_PAGE;
    public static String QUNIT_TESTS_PAGE;
    public static String CLEAR_DB_ENDPOINT;
    public static String LINK_TEST_PAGE;
    public static String LOGOUT_PAGE;
    public static String USERS_PAGE;
    public static String USER_PAGE;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractWebDriverIntTst.class);
    private static boolean running;

    public static final int PERIOD_TO_WAIT_FOR_CHANGES = 1000;


    public static final String ONE_PARA_TEST_PAGE_PATH = "/one-para.html";

    static {
        BASE_URL = System.getProperty("baseUrl");

        if (BASE_URL == null || BASE_URL.equals("")) {
            BASE_URL = "http://localhost:8080";
        }

        // builtin stuff
        LOGIN_PAGE = BASE_URL + PathsAndRoutes.STATIK_LOGIN;
        LOGOUT_PAGE = BASE_URL + PathsAndRoutes.STATIK_LOGOUT;
        LOGIN_ERROR_PAGE = BASE_URL + PathsAndRoutes.STATIK_LOGIN_ERROR;
        CLEAR_DB_ENDPOINT = BASE_URL + PathsAndRoutes.STATIK_CLEAR_DB;
        USERS_PAGE = BASE_URL + PathsAndRoutes.STATIK_ADMIN_USERS;
        USER_PAGE = BASE_URL + PathsAndRoutes.STATIK_ADMIN_USER;

        // test pages in test-website directory
        ROOT_PAGE = BASE_URL + "/index.html";
        ONE_PARA_TEST_PAGE = BASE_URL + ONE_PARA_TEST_PAGE_PATH;
        TWO_PARA_TEST_PAGE = BASE_URL + "/two-paras.html";
        LIST_TEST_PAGE = BASE_URL + "/list.html";
        QUNIT_TESTS_PAGE = BASE_URL + "/qunit-tests.html";
        LINK_TEST_PAGE = BASE_URL + "/links.html";
    }

    @BeforeClass
    public static void classSetUp() {
        if (driver != null) {
            driver.manage().deleteAllCookies();
        }

        if (running) {
            return;
        }

        running = true;
        driver = runningDriver();
    }


    @After
    public void clearContentItemsCollection() {
        driver.get(CLEAR_DB_ENDPOINT);
    }


    protected static WebDriver runningDriver() {
        WebDriver d = createDriver();
        addHookToShutdownDriver(d);
        return d;
    }

    protected WebElement findEventually(final By selector) {
        WebDriverWait wait = new WebDriverWait(driver, 3);
        return (WebElement) wait.until(new Function<WebDriver, Object>() {
            @Override
            public Object apply(WebDriver webDriver) {
                return driver.findElement(selector);
            }
        });
    }

    protected void waitForPresenceOfItemByClassName(final String className) {
        findEventually(By.className(className));
    }

    protected void waitForPresenceOfItemById(final String id) {
        findEventually(By.id(id));
    }

    protected void sendLogin(String wrongUsername, String wrongPassword) {
        try {
            assertEquals("Should show sign in page", "Sign in", driver.findElement(By.tagName("h1")).getText());
        } catch (NoSuchElementException e) {
            LOG.error(driver.getPageSource());
        }

        WebElement username = driver.findElement(By.name("username"));
        WebElement password = driver.findElement(By.name("password"));

        username.clear();
        username.sendKeys(wrongUsername);
        password.clear();
        password.sendKeys(wrongPassword);

        password.submit();
    }

    protected void doLoginWith(String username, String password) {
        driver.get(LOGIN_PAGE);
        sendLogin(username, password);
    }


    protected void doLogout() {
        driver.get(LOGOUT_PAGE);
    }

    protected static WebDriver createDriver() {
        FirefoxProfile profile = new FirefoxProfile();
//        profile.setEnableNativeEvents(true);
        FirefoxDriver driver = new FirefoxDriver(profile);
        return driver;
    }

    protected static void addHookToShutdownDriver(final WebDriver d) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    d.quit();
                } catch (Exception e) {
                    // ignore
                }
            }
        });
    }


    protected void copy(WebElement el) {
        Actions a = new Actions(driver);
        a.contextClick(el);
        a.perform();

        WebElement menu = driver.findElement(By.id("jqContextMenu"));
        menu.findElement(By.id("copy")).click();
    }

    protected void sleepForMs(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            //
        }
    }

    protected void changeContentOf(WebElement el, String newContent, Language lang) {
        LOG.debug("Triggering context menu on " + el.getTagName());
        Actions a = new Actions(driver);
        a.contextClick(el);
        a.build().perform();

        WebElement menu = driver.findElement(By.id("jqContextMenu"));
        menu.findElement(By.id("edit")).click();

        LOG.debug("Waiting for editor");
        waitForPresenceOfItemByClassName("editor-sandbox");

        LOG.debug("Changing language");
        driver.findElement(By.id("language-switcher")).findElement(By.xpath("./option[@value='" + lang.code() + "']")).click();
        sleepForMs(PERIOD_TO_WAIT_FOR_CHANGES);

        LOG.debug("Setting content");
        ((FirefoxDriver) driver).executeScript("document.editor.composer.setValue('" + newContent + "')");
        sleepForMs(PERIOD_TO_WAIT_FOR_CHANGES);

        LOG.debug("Submitting");
        driver.findElement(By.id("submit")).click();

        LOG.debug("Switching frames again");
        driver.switchTo().defaultContent();
    }

    protected void changeContentOf(WebElement el, String newContent) {
        changeContentOf(el, newContent, Language.English);
    }

    protected void changeContentOf(String tag, String newContent) {
        WebElement para = driver.findElement(By.cssSelector(tag));
        changeContentOf(para, newContent);
    }

}
