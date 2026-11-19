package org.autotests.tests;

import io.qameta.allure.*;
import org.autotests.pages.CartPage;
import org.autotests.pages.CheckoutPage;
import org.autotests.pages.InventoryPage;
import org.autotests.pages.LoginPage;
import org.autotests.utils.WaitUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("E-commerce")
@Feature("Корзина и оформление заказа")
@Execution(ExecutionMode.SAME_THREAD)
// @ExtendWith(ScreenshotExtension.class) УДАЛЕНО: теперь это есть в BaseTest
public class CartTest extends BaseTest { // <-- ДОБАВЛЕНО: наследуем всю магию BaseTest

    LoginPage loginPage;
    InventoryPage inventoryPage;

    /**
     * Минимальный setup: инициализируем только Page Objects.
     * Браузер (driver) уже создан и настроен в BaseTest.setUpDriver().
     */
    @BeforeEach
    void setUpPages() {
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
    }

    @Test
    @Story("Добавление товара в корзину")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверка добавления товара Sauce Labs Backpack в корзину")
    @Owner("Alexey Michkov")
    void addToCartTest() {
        loginPage.login("standard_user", "secret_sauce");
        inventoryPage.addToCartBackpack();

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
        loginPage.login("standard_user", "secret_sauce");

        // Было: WebDriverWait wait = new WebDriverWait(...)
        // Стало: используем наш централизованный WaitUtils
        WaitUtils.waitForVisible(driver, By.id("add-to-cart-sauce-labs-backpack"));

        inventoryPage.addToCartBackpack();
        inventoryPage.addToCartBikeLight();
        assertThat(inventoryPage.getCartBadgeText()).isEqualTo("2");

        CartPage cartPage = inventoryPage.goToCart();
        assertThat(cartPage.getPageTitle()).isEqualTo("Your Cart");

        CheckoutPage checkoutPage = cartPage.goToCheckout();
        checkoutPage.fillInfo("Alexey", "Michkov", "123456");

        // БЫЛО: try { Thread.sleep(6000); } catch (InterruptedException e) {}
        // СТАЛО: УДАЛЕНО. Паузы — это антипаттерн.
        // Если переход медленный, правильный WaitUtils вызов должен быть ВНУТРИ метода clickContinue() в Page Object.

        checkoutPage.clickContinue();
        checkoutPage.waitForOverviewPage();
        assertThat(checkoutPage.getPageTitle()).isEqualTo("Checkout: Overview");

        checkoutPage.clickFinish();
        assertThat(checkoutPage.getCompleteHeaderText())
                .isEqualTo("Thank you for your order!");

        System.out.println("✅ Полный заказ через POM оформлен!");
    }
}