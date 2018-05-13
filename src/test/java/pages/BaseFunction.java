package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BaseFunction {

    public WebDriver driver;

    private static final String CHROME_DRIVER_LINUX64 = "./drivers/linux/x64/Chrome/chromedriver";
    private static final String FIREFOX_DRIVER_LINUX64 = "./drivers/linux/x64/Firefox/geckodriver";
    private static final String CHROME_DRIVER_WIN32 = "./drivers/windows/win32/Chrome/chromedriver.exe";
    private static final String FIREFOX_DRIVER_WIN64 = "./drivers/windows/win64/Firefox/geckodriver.exe";

    private static final String GECKO_PROPERTY = "webdriver.gecko.driver";
    private static final String CHROME_PROPERTY = "webdriver.chrome.driver";


    private static final Logger LOGGER = LogManager.getLogger(BaseFunction.class);

    public BaseFunction() {
        this.initDriver();
    }

    public void initDriver() {
        System.setProperty(CHROME_PROPERTY, CHROME_DRIVER_LINUX64);
        this.driver = new ChromeDriver();
        LOGGER.info("Starting WEB Browser");
        WebDriverWait wait = new WebDriverWait(driver, 10);
        driver.manage().window().maximize();
    }

    public void goToUrl(String url) {
        if (!url.contains("http://") && !url.contains("https://")) {
            url = "http://" + url;
        }
        LOGGER.info("Opening " + url);
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

    }

    public List<WebElement> getElements(By locator) {
        LOGGER.info("Getting elements: " + locator);
        return driver.findElements(locator);
    }

    public WebElement getElement(By locator) {
//        LOGGER.info("Getting element: " + locator);
        return driver.findElement(locator);
    }

    public String extractCurrencyAcronym(String fullString) {
        return fullString.substring(0, 3); // Extracts USD from full string USD - ASV dolārs
    }

    public void selectDropdownValue(By DropdownLocator, String selectTitle) {
        Select dropDown = new Select(getElement(DropdownLocator));
        dropDown.selectByVisibleText(selectTitle);
    }

    public void click(By locator) {
        LOGGER.info("Clicking on element: " + locator);
        driver.findElement(locator).click();
    }

    public void quitDriver() {
        driver.quit();
    }

}
