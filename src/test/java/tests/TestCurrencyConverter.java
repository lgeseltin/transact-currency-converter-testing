package tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BaseFunction;
import pages.Currencies;
import pages.Math;

import java.util.ArrayList;
import java.util.List;

public class TestCurrencyConverter {
    private static final Logger LOGGER = LogManager.getLogger(TestCurrencyConverter.class);
    private static final String URL = "https://www.luminor.lv/lv/valutu-kursi";

    private static final By SELL_DROPDOWN = By.id("currency-sell-select");
    private static final By BUY_DROPDOWN = By.id("currency-buy-select");
    private static final By SELL_TEXTFIELD = By.id("currency-sell-textfield");
    private static final By BUY_TEXTFIELD = By.id("currency-buy-textfield");

    private static final Double MONEY = 100.00;

    private BaseFunction baseFunction = new BaseFunction();
    private Math math = new Math();

    List<Double> sellCurrency = new ArrayList<>();
    List<Double> buyCurrency = new ArrayList<>();
    List<Double> ecbCurrency = new ArrayList<>();

    List<Double> calculatedSellReference = new ArrayList<>();
    List<Double> calculatedBuyReference = new ArrayList<>();

    List<Double> calculatedBuyOnSite = new ArrayList<>();
    List<Double> calculatedSellOnSite = new ArrayList<>();

    @BeforeMethod
    public void goToUrl() {
        baseFunction.goToUrl(URL);
    }

    @Test(priority = 1, alwaysRun = true)
    public void collectBuyRatesToList() {
        for (int i = 1; i < Currencies.values().length + 1; i++) {
            if (baseFunction.getElement(By.xpath("//*[@id='currency-rates-table']/tr[" + i + "]/td[3]")).getText() == null) {
                buyCurrency.add(0.00);
            } else {
                Double value = Double.parseDouble(baseFunction.getElement(By.xpath("//*[@id='currency-rates-table']/tr[" + i + "]/td[3]")).getText());
                buyCurrency.add(value);
            }
        }
        LOGGER.info(buyCurrency);
        Assert.assertNotNull(buyCurrency);
    }

    @Test(priority = 2, alwaysRun = true)
    public void collectSellRatesToList() {
        for (int i = 1; i < Currencies.values().length + 1; i++) {
            if (baseFunction.getElement(By.xpath("//*[@id='currency-rates-table']/tr[" + i + "]/td[2]")).getText() == null) {
                sellCurrency.add(0.00);
            } else {
                Double value = Double.parseDouble(baseFunction.getElement(By.xpath("//*[@id='currency-rates-table']/tr[" + i + "]/td[2]")).getText());
                sellCurrency.add(value);
            }
        }
        LOGGER.info(sellCurrency);
        Assert.assertNotNull(sellCurrency);
    }

    @Test(priority = 3)
    public void collectECBRatesToList() {
        for (int i = 1; i < Currencies.values().length + 1; i++) {
            if (baseFunction.getElement(By.xpath("//*[@id='currency-rates-table']/tr[" + i + "]/td[4]")).getText() == null) {
                ecbCurrency.add(0.00);
            } else {
                Double value = Double.parseDouble(baseFunction.getElement(By.xpath("//*[@id='currency-rates-table']/tr[" + i + "]/td[4]")).getText());
                ecbCurrency.add(value);
            }
        }
        LOGGER.info(ecbCurrency);
        Assert.assertNotNull(ecbCurrency);
    }

    @Test(priority = 4, alwaysRun = true)
    public void calculateSellCurrencyOnSiteAndAddToList() {
        for (Currencies currencyTitle : Currencies.values()) {
            baseFunction.selectDropdownValue(SELL_DROPDOWN, currencyTitle.name());
            baseFunction.getElement(SELL_TEXTFIELD).sendKeys(String.valueOf(MONEY));
            Double value = Double.parseDouble(baseFunction.getElement(BUY_TEXTFIELD).getAttribute("value"));
            calculatedSellOnSite.add(value);
        }
        LOGGER.info("Calculated sell on site: " + calculatedSellOnSite);
        Assert.assertNotNull(calculatedSellOnSite);
    }

    @Test(priority = 5, alwaysRun = true)
    public void calculateBuyCurrencyOnSiteAndAddToList() {
        for (Currencies currencyTitle : Currencies.values()) {
            baseFunction.selectDropdownValue(BUY_DROPDOWN, currencyTitle.name());

            WebElement inputField = baseFunction.getElement(BUY_TEXTFIELD);
            while (inputField.getAttribute("value").length() > 0) {
                inputField.sendKeys(Keys.BACK_SPACE);
            }
            inputField.sendKeys(String.valueOf(MONEY));
            Double value = Double.parseDouble(baseFunction.getElement(SELL_TEXTFIELD).getAttribute("value"));
            calculatedBuyOnSite.add(value);
        }
        LOGGER.info("Calculated buy on site: " + calculatedBuyOnSite);
        Assert.assertNotNull(calculatedBuyOnSite);
    }

    @Test(priority = 6, dependsOnMethods = {"collectBuyRatesToList", "calculateSellCurrencyOnSiteAndAddToList"})
    public void calculateSellCurrenciesOffline() {
        for (Double rate : buyCurrency) {
            double number = math.convertCurrencyByRate(MONEY, rate);
            double roundedCalculations = math.roundToTwoDecimals(number);
            calculatedSellReference.add(roundedCalculations);
        }
        LOGGER.info("calculated sell reference" + calculatedSellReference);
        Assert.assertEquals(calculatedSellOnSite, calculatedSellReference);
    }

    @Test(priority = 7, dependsOnMethods = {"collectSellRatesToList", "calculateBuyCurrencyOnSiteAndAddToList"})
    public void calculateBuyCurrenciesOffline() {
        for (Double rate : sellCurrency) {
            double number = math.convertCurrencyByRate(MONEY, rate);
            double roundedCalculations = math.roundToTwoDecimals(number);
            calculatedBuyReference.add(roundedCalculations);
        }
        LOGGER.info("calculated buy reference" + calculatedBuyReference);
        Assert.assertEquals(calculatedBuyOnSite, calculatedBuyReference);
    }

    @AfterClass
    public void closeBrowser() {
        LOGGER.info("Browser closed gracefully");
        baseFunction.quitDriver();
    }
}
