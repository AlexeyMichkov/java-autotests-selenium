package org.autotests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage {

    private WebDriver driver;

    // Локаторы
    private By pageTitle = By.className("title");
    private By checkoutButton = By.id("checkout");

    // Конструктор
    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    // Методы
    public String getPageTitle() {
        return driver.findElement(pageTitle).getText();
    }

    // Переход к оформлению заказа — возвращает объект CheckoutPage
    public CheckoutPage goToCheckout() {
        driver.findElement(checkoutButton).click();
        return new CheckoutPage(driver);
    }
}