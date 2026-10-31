package org.autotests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class CheckoutPage {

    private WebDriver driver;

    // Локаторы
    private By firstNameField = By.id("first-name");
    private By lastNameField = By.name("lastName");
    private By postalCodeField = By.id("postal-code");
    private By continueButton = By.id("continue");
    private By finishButton = By.id("finish");
    private By completeHeader = By.className("complete-header");
    private By pageTitle = By.className("title");

    // Конструктор
    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }

    // Методы
    public void fillInfo(String firstName, String lastName, String postalCode) {
        driver.findElement(firstNameField).sendKeys(firstName);
        driver.findElement(lastNameField).sendKeys(lastName);
        driver.findElement(postalCodeField).sendKeys(postalCode);
    }

    public void clickContinue() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        button.click();
    }

    // Ожидание загрузки страницы Overview
    public void waitForOverviewPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Ждём, пока URL не будет содержать "checkout-step-two" (страница Overview)
        wait.until(ExpectedConditions.urlContains("checkout-step-two"));

        // И дополнительно ждём, пока заголовок не станет "Checkout: Overview"
        wait.until(ExpectedConditions.textToBe(pageTitle, "Checkout: Overview"));
    }

    public String getPageTitle() {
        return driver.findElement(pageTitle).getText();
    }

    public void clickFinish() {
        driver.findElement(finishButton).click();
    }

    public String getCompleteHeaderText() {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement successHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(completeHeader));
            return successHeader.getText();
        }
    }

