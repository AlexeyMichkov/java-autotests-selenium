package org.autotests.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.autotests.pages.LoginPage;
import org.autotests.pages.InventoryPage;
import org.autotests.pages.CartPage;
import org.autotests.pages.CheckoutPage;

import static org.assertj.core.api.Assertions.assertThat;
import io.qameta.allure.*;

@Epic("E-commerce")
@Feature("Корзина и оформление заказа")

public class CartTest {

    WebDriver driver;
    LoginPage loginPage;
    InventoryPage inventoryPage;

    @BeforeEach
    void setUp() {
        // Настройки Chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-data-dir=C:\\temp\\chrome-profile-" + System.currentTimeMillis());
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-features=PasswordManager,PasswordManagerRedesign");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-autofill");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Создаём объекты страниц
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Story("Добавление товара в корзину")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверка добавления товара Sauce Labs Backpack в корзину")
    @Owner("Alexey Michkov")
    void addToCartTest() {
        // Логин через Page Object
        loginPage.login("standard_user", "secret_sauce");

        // Добавляем товар
        inventoryPage.addToCartBackpack();

        // Проверяем счётчик
        String badge = inventoryPage.getCartBadgeText();
        assertThat(badge).isEqualTo("1");

        System.out.println("✅ POM тест пройден! Счётчик = " + badge);
    }

    @Test
    @Story("Полное оформление заказа")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Проверка полного цикла оформления заказа: от добавления товара до подтверждения")
    @Owner("Alexey Michkov")
    @Link(name = "Требование", url = "https://saucedemo.com/checkout")
    void fullCheckoutTest() {
        // Логин
        loginPage.login("standard_user", "secret_sauce");

        // Добавляем два товара
        inventoryPage.addToCartBackpack();
        inventoryPage.addToCartBikeLight();
        assertThat(inventoryPage.getCartBadgeText()).isEqualTo("2");

        // Переходим в корзину
        CartPage cartPage = inventoryPage.goToCart();
        assertThat(cartPage.getPageTitle()).isEqualTo("Your Cart");

        // Оформляем заказ
        CheckoutPage checkoutPage = cartPage.goToCheckout();
        checkoutPage.fillInfo("Alexey", "Michkov", "123456");
        checkoutPage.clickContinue();
        checkoutPage.waitForOverviewPage();
        assertThat(checkoutPage.getPageTitle()).isEqualTo("Checkout: Overview");

        checkoutPage.clickFinish();
        assertThat(checkoutPage.getCompleteHeaderText())
                .isEqualTo("Thank you for your order!");

        System.out.println("✅ Полный заказ через POM оформлен!");
    }
}