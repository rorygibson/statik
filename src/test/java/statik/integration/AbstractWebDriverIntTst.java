package statik.integration;

import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

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

    private static final Logger LOG = Logger.getLogger(AbstractWebDriverIntTst.class);
    private static boolean running;


    static {
        BASE_URL = System.getProperty("baseUrl");

        if (BASE_URL == null || BASE_URL.equals("")) {
            BASE_URL = "http://localhost:8080/";
        }

        if (!BASE_URL.endsWith("/")) {
            BASE_URL = BASE_URL + "/";
        }

        ROOT_PAGE = BASE_URL + "index.html";
        LOGIN_ERROR_PAGE = BASE_URL + "login-error";
        LOGIN_PAGE = BASE_URL + "login";
        LOGOUT_PAGE = BASE_URL + "logout";
        ONE_PARA_TEST_PAGE = BASE_URL + "one-para.html";
        TWO_PARA_TEST_PAGE = BASE_URL + "two-paras.html";
        LIST_TEST_PAGE = BASE_URL + "list.html";
        QUNIT_TESTS_PAGE = BASE_URL + "qunit-tests.html";
        CLEAR_DB_ENDPOINT = BASE_URL + "clear-db";
        LINK_TEST_PAGE = BASE_URL + "links.html";
    }

    @BeforeClass
    public static void setUp() {
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

    protected void waitForPresenceOf(final String tagName) {
        WebDriverWait wait = new WebDriverWait(driver, 3);
        wait.until(new Function<WebDriver, Object>() {
            @Override
            public Object apply(WebDriver webDriver) {
                return driver.findElement(By.tagName(tagName));
            }
        });
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


    protected void doLogout() {
        driver.get(LOGOUT_PAGE);
    }

    protected static WebDriver createDriver() {
        WebDriver d;

        String phantomBinaryLocation = System.getProperty("phantomBinary");
        if (StringUtils.isNotBlank(phantomBinaryLocation)) {
            LOG.info("Using phantomjs at " + phantomBinaryLocation);
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomBinaryLocation);

            d = new PhantomJSDriver(caps);
        } else {
            LOG.info("Using Firefox");
            d = new FirefoxDriver();
        }
        return d;
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

}
