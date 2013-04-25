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

    public static final String ROOT_PAGE = "http://localhost:8080/";
    public static final String LOGIN_ERROR_PAGE = "http://localhost:8080/login-error";
    public static final String LOGIN_PAGE = "http://localhost:8080/login";
    public static final String ONE_PARA_TEST_PAGE = "http://localhost:8080/one-para.html";
    public static final String TWO_PARA_TEST_PAGE = "http://localhost:8080/two-paras.html";
    public static final String LIST_TEST_PAGE = "http://localhost:8080/list.html";
    public static final String QUNIT_TESTS_PAGE = "http://localhost:8080/qunit-tests.html";
    public static final String CLEAR_DB_ENDPOINT = "http://localhost:8080/clear-db";
    public static final String LINK_TEST_PAGE = "http://localhost:8080/links.html";

    private static final Logger LOG = Logger.getLogger(AbstractWebDriverIntTst.class);
    private static boolean running;

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

    protected static WebDriver createDriver() {
        WebDriver d = null;

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
