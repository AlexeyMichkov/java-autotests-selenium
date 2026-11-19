package org.autotests.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public final class WaitUtils {

    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private WaitUtils() { }

    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return waitForVisible(driver, locator, DEFAULT_TIMEOUT);
    }

    public static WebElement waitForVisible(WebDriver driver, By locator, Duration timeout) {
        return waitUntil(driver, timeout,
                ExpectedConditions.visibilityOfElementLocated(locator), "видимым", locator);
    }

    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return waitForClickable(driver, locator, DEFAULT_TIMEOUT);
    }

    public static WebElement waitForClickable(WebDriver driver, By locator, Duration timeout) {
        return waitUntil(driver, timeout,
                ExpectedConditions.elementToBeClickable(locator), "кликабельным", locator);
    }

    public static void waitForText(WebDriver driver, By locator, String text) {
        waitUntil(driver, DEFAULT_TIMEOUT,
                ExpectedConditions.textToBe(locator, text), "с текстом «" + text + "»", locator);
    }

    public static void waitForUrlContains(WebDriver driver, String urlPart) {
        waitUntil(driver, DEFAULT_TIMEOUT,
                ExpectedConditions.urlContains(urlPart),
                "URL со строкой «" + urlPart + "»", null);
    }

    public static List<WebElement> waitForVisibleList(WebDriver driver, By locator) {
        return waitUntil(driver, DEFAULT_TIMEOUT,
                ExpectedConditions.visibilityOfAllElementsLocatedBy(locator),
                "видимым (список)", locator);
    }

    public static void clickWhenReady(WebDriver driver, By locator) {
        waitUntil(driver, DEFAULT_TIMEOUT,
                ExpectedConditions.elementToBeClickable(locator),
                "кликабельным перед кликом", locator).click();
    }

    public static String getTextWhenVisible(WebDriver driver, By locator) {
        return waitForVisible(driver, locator).getText();
    }

    private static <T> T waitUntil(WebDriver driver, Duration timeout,
                                   Function<WebDriver, T> condition,
                                   String actionDescription, By locator) {
        try {
            return new WebDriverWait(driver, timeout).until(condition);
        } catch (TimeoutException e) {
            String target = locator != null ? "Элемент [" + locator + "]" : "Страница";
            throw new AssertionError(target + " так и не стал " + actionDescription
                    + " за " + timeout.getSeconds() + " сек.", e);
        }
    }
}