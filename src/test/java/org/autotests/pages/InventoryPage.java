package org.autotests.pages;

import org.autotests.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage {

    private final WebDriver driver;

    // Локаторы
    private final By pageTitle = By.className("title");
    private final By cartBadge = By.className("shopping_cart_badge");
    private final By cartLink = By.className("shopping_cart_link");
    private final By addToCartBackpack = By.id("add-to-cart-sauce-labs-backpack");
    private final By addToCartBikeLight = By.id("add-to-cart-sauce-labs-bike-light");

    // Конструктор
    public InventoryPage(WebDriver driver) {
        this.driver = driver;
    }

    // Методы с использованием WaitUtils
    public String getPageTitle() {
        return WaitUtils.getTextWhenVisible(driver, pageTitle);
    }

    public String getCartBadgeText() {
        return WaitUtils.getTextWhenVisible(driver, cartBadge);
    }

    public void addToCartBackpack() {
        WaitUtils.clickWhenReady(driver, addToCartBackpack);
    }

    public void addToCartBikeLight() {
        WaitUtils.clickWhenReady(driver, addToCartBikeLight);
    }

    // Переход в корзину — возвращает объект CartPage
    public CartPage goToCart() {
        WaitUtils.clickWhenReady(driver, cartLink);
        return new CartPage(driver);
    }
}