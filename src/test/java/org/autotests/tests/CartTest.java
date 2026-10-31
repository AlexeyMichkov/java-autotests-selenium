package org.autotests.tests;

import org.junit.jupiter.api.extension.ExtendWith;
import org.autotests.utils.ScreenshotExtension;
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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import io.qameta.allure.*;

@ExtendWith(ScreenshotExtension.class)
@Epic("E-commerce")
@Feature("Корзина и оформление заказа")

public class CartTest {

    WebDriver driver;
    LoginPage loginPage;
    InventoryPage inventoryPage;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();

        // Headless режим
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        // Остальные аргументы
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-features=PasswordManager,PasswordManagerRedesign");
        options.addArguments("--disable-notifications");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
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

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-to-cart-sauce-labs-backpack")));

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
        // Добавил паузу между вводом и кликом
        try { Thread.sleep(6000); } catch (InterruptedException e) {}

        checkoutPage.clickContinue();
        checkoutPage.waitForOverviewPage();
        assertThat(checkoutPage.getPageTitle()).isEqualTo("Checkout: Overview");

        checkoutPage.clickFinish();
        assertThat(checkoutPage.getCompleteHeaderText())
                .isEqualTo("Thank you for your order!");

        System.out.println("✅ Полный заказ через POM оформлен!");
    }
}