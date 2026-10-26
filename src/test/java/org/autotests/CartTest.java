package org.autotests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;
import org.openqa.selenium.chrome.ChromeOptions;

public class CartTest {

    WebDriver driver;

    @BeforeEach
    void setUp() {
        // Настройки Chrome
        org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();

        // Отключаем менеджер паролей
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-features=PasswordManager");

        // Отключаем уведомления
        options.addArguments("--disable-notifications");

        // Создаём драйвер с настройками
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    // Вспомогательный метод — логин (чтобы не дублировать код)
    private void login() {
        driver.get("https://www.saucedemo.com/");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
    }

    @Test
    void addToCartTest() {
        // 1. Логинимся
        login();

        // 2. Нажимаем "Add to cart" у первого товара (Sauce Labs Backpack)
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();

        // 3. Проверяем, что счётчик корзины = 1
        String cartBadge = driver.findElement(By.className("shopping_cart_badge")).getText();
        assertThat(cartBadge).isEqualTo("1");

        System.out.println("✅ Товар добавлен в корзину, счётчик = " + cartBadge);
    }

    @Test
    void addToCartAndCheckoutTest() {
        // 1. Логинимся
        login();

        // 2. Добавляем два товара
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();

        // 3. Проверяем счётчик = 2
        String cartBadge = driver.findElement(By.className("shopping_cart_badge")).getText();
        assertThat(cartBadge).isEqualTo("2");

        // 4. Переходим в корзину
        driver.findElement(By.className("shopping_cart_link")).click();
        assertThat(driver.getCurrentUrl()).contains("cart.html");
        assertThat(driver.findElement(By.className("title")).getText()).isEqualTo("Your Cart");

        // 5. Нажимаем Checkout
        driver.findElement(By.id("checkout")).click();

        // 6. Заполняем форму оформления заказа
        driver.findElement(By.id("first-name")).sendKeys("Alexey");
        driver.findElement(By.name("lastName")).sendKeys("Michkov");
        driver.findElement(By.id("postal-code")).sendKeys("123456");

        // 7. Нажимаем Continue
        driver.findElement(By.id("continue")).click();

        // 8. ЖДЁМ, пока загрузится страница Overview
        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10));
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .visibilityOfElementLocated(By.className("title")));

// Проверяем, что заголовок правильный
        String currentTitle = driver.findElement(By.className("title")).getText();
        System.out.println("Текущий заголовок: " + currentTitle);

        // 9. Проверяем, что мы на странице Overview
        assertThat(driver.findElement(By.className("title")).getText()).isEqualTo("Checkout: Overview");

        // 10. Нажимаем Finish
        driver.findElement(By.id("finish")).click();

        // 11. Проверяем, что заказ оформлен
        String completeText = driver.findElement(By.className("complete-header")).getText();
        assertThat(completeText).isEqualTo("Thank you for your order!");

        System.out.println("✅ Заказ успешно оформлен!");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        // Опционально: удалить временный профиль
        // (но это усложнит код, пока не обязательно)
    }
}