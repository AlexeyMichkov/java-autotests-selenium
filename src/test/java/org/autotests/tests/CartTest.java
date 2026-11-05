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
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@ExtendWith(ScreenshotExtension.class)
@Epic("E-commerce")
@Feature("Корзина и оформление заказа")
@Execution(ExecutionMode.SAME_THREAD)

public class CartTest {

    WebDriver driver;
    LoginPage loginPage;
    InventoryPage inventoryPage;

    /**
     * Метод инициализации браузера перед каждым тестом.
     *
     * Проверяет системное свойство "headed":
     * - Если headed=true → запускаем браузер в обычном режиме (видимое окно)
     * - Если headed=false или не задано → запускаем в headless режиме (без окна)
     *
     * Headless режим используется по умолчанию для быстрого прогона тестов в CI/CD.
     * Headed режим удобен для отладки и визуальной проверки.
     */
    @BeforeEach
    void setUp() {
        // Создаём объект настроек Chrome
        ChromeOptions options = new ChromeOptions();

        // Проверяем, в каком режиме нужно запустить браузер
        // Значение по умолчанию — false (headless режим)
        boolean isHeaded = Boolean.parseBoolean(System.getProperty("headed", "false"));

        if (!isHeaded) {
            // Headless режим: браузер работает без видимого окна
            // Это быстрее и подходит для CI/CD пайплайнов
            options.addArguments("--headless=new");
            System.out.println("🔹 Запуск в HEADLESS режиме (без видимого окна)");
        } else {
            // Headed режим: браузер открывается как обычное окно
            // Удобно для отладки и ручной проверки
            System.out.println("🔹 Запуск в HEADED режиме (с видимым окном браузера)");
        }

        // Обязательные аргументы для стабильной работы Chrome
        options.addArguments("--no-sandbox");                    // Отключаем песочницу (нужно для Linux/Docker)
        options.addArguments("--disable-dev-shm-usage");         // Используем /tmp вместо /dev/shm
        options.addArguments("--disable-gpu");                   // Отключаем GPU-ускорение (для headless)
        options.addArguments("--window-size=1920,1080");         // Фиксированный размер окна

        // Аргументы для улучшения UX при автоматизации
        options.addArguments("--disable-save-password-bubble");  // Отключаем предложение сохранить пароль
        options.addArguments("--disable-features=PasswordManager,PasswordManagerRedesign"); // Отключаем менеджер паролей
        options.addArguments("--disable-notifications");         // Отключаем уведомления браузера

        // Создаём экземпляр драйвера с указанными настройками
        driver = new ChromeDriver(options);

        // Разворачиваем окно браузера на весь экран
        driver.manage().window().maximize();

        // Инициализируем Page Objects для работы со страницами
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Тест-кейс: TC-002 - Добавление товара в корзину
     *
     * @see <a href="https://github.com/AlexeyMichkov/java-autotests-selenium/issues/2">
     *      TC-002 на GitHub</a>
     */
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