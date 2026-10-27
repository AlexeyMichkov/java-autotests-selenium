package org.autotests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
        driver.findElement(continueButton).click();
    }

    // Ожидание загрузки страницы Overview
    public void waitForOverviewPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
    }

    public String getPageTitle() {
        return driver.findElement(pageTitle).getText();
    }

    public void clickFinish() {
        driver.findElement(finishButton).click();
    }

    public String getCompleteHeaderText() {
        return driver.findElement(completeHeader).getText();
    }
}