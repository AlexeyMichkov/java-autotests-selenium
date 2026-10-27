package org.autotests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage {

    private WebDriver driver;

    // Локаторы
    private By pageTitle = By.className("title");
    private By cartBadge = By.className("shopping_cart_badge");
    private By cartLink = By.className("shopping_cart_link");
    private By addToCartBackpack = By.id("add-to-cart-sauce-labs-backpack");
    private By addToCartBikeLight = By.id("add-to-cart-sauce-labs-bike-light");

    // Конструктор
    public InventoryPage(WebDriver driver) {
        this.driver = driver;
    }

    // Методы
    public String getPageTitle() {
        return driver.findElement(pageTitle).getText();
    }

    public String getCartBadgeText() {
        return driver.findElement(cartBadge).getText();
    }

    public void addToCartBackpack() {
        driver.findElement(addToCartBackpack).click();
    }

    public void addToCartBikeLight() {
        driver.findElement(addToCartBikeLight).click();
    }

    // Переход в корзину — возвращает объект CartPage
    public CartPage goToCart() {
        driver.findElement(cartLink).click();
        return new CartPage(driver);
    }
}