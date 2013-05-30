package statik.integration;

import com.google.common.base.Function;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statik.route.PathsAndRoutes;

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

    public static final int PERIOD_TO_WAIT_FOR_EDITOR = 500;
    public static final int PERIOD_TO_WAIT_FOR_CHANGES = 500;


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
        ONE_PARA_TEST_PAGE = BASE_URL + "/one-para.html";
        TWO_PARA_TEST_PAGE = BASE_URL + "/two-paras.html";
        LIST_TEST_PAGE = BASE_URL + "/list.html";
        QUNIT_TESTS_PAGE = BASE_URL + "/qunit-tests.html";
        LINK_TEST_PAGE = BASE_URL + "/links.html";
    }

    @BeforeClass
    public static void setUp() {
        if (running) {
            return;
        }
        running = true;

        driver = runningDriver();
    }


    public class DumpSourceOnFailureStatement extends Statement {

        private final Statement base;

        public DumpSourceOnFailureStatement(Statement base) {
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                base.evaluate();
            } catch (Throwable t) {
                LOG.error(driver.getPageSource());
                throw t;
            }
        }
    }


    @Rule
    public MethodRule dumpSourceOnFailure = new MethodRule() {
        @Override
        public Statement apply(Statement statement, FrameworkMethod frameworkMethod, Object o) {
            return new DumpSourceOnFailureStatement(statement);
        }
    };


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

    protected void waitForPresenceOf(final String tagName) {
        findEventually(By.tagName(tagName));
    }

    protected void sendLogin(String wrongUsername, String wrongPassword) {
        try {
            assertEquals("Should show sign in page", "Sign in", driver.findElement(By.tagName("h1")).getText());
        } catch (NoSuchElementException e) {
            LOG.error(driver.getPageSource());
        }

        WebElement username = driver.findElement(By.name("username"));
        WebElement password = driver.findElement(By.name("password"));

        username.sendKeys(wrongUsername);
        password.sendKeys(wrongPassword);

        password.submit();
    }

    protected void doLoginWith(String username, String password) {
        driver.get(LOGIN_PAGE);
        sendLogin(username, password);
    }


    protected void doLogout() {
        driver.switchTo().frame("control-box");
        driver.findElement(By.id("logout")).click();
        driver.switchTo().defaultContent();
    }

    protected static WebDriver createDriver() {
        return new FirefoxDriver();
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

    protected void changeContentOf(WebElement el, String newContent) {
        Actions a = new Actions(driver);
        a.contextClick(el);
        a.perform();

        WebElement menu = driver.findElement(By.id("jqContextMenu"));
        menu.findElement(By.id("edit")).click();

        waitForPresenceOf("iframe");

        sleepForMs(PERIOD_TO_WAIT_FOR_EDITOR);
        ((FirefoxDriver) driver).executeScript("document.editor.composer.setValue('" + newContent + "')");
        sleepForMs(PERIOD_TO_WAIT_FOR_CHANGES);
        driver.findElement(By.id("submit")).click();
        sleepForMs(PERIOD_TO_WAIT_FOR_CHANGES);
    }

    protected void changeContentOf(String tag, String newContent) {
        WebElement para = driver.findElement(By.cssSelector(tag));
        changeContentOf(para, newContent);
    }

}
