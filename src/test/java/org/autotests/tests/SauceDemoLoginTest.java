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

import static org.assertj.core.api.Assertions.assertThat;
import io.qameta.allure.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@ExtendWith(ScreenshotExtension.class)
@Epic("E-commerce")
@Feature("Аутентификация")
@Execution(ExecutionMode.SAME_THREAD)
public class SauceDemoLoginTest {

    WebDriver driver;
    LoginPage loginPage;

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

        // Инициализируем Page Object для страницы логина
        loginPage = new LoginPage(driver);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Тест-кейс: TC-001 - Успешный логин стандартного пользователя
     *
     * @see <a href="https://github.com/AlexeyMichkov/java-autotests-selenium/issues/1">
     *      TC-001 на GitHub</a>
     */
    @Test
    @Story("Успешный вход в систему")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверка успешного логина с валидными учётными данными")
    @Owner("Alexey Michkov")
    void successfulLoginTest() {
        loginPage.login("standard_user", "secret_sauce");

        // После логина попадаем на InventoryPage — проверяем URL
        assertThat(driver.getCurrentUrl()).contains("inventory.html");

        System.out.println("✅ Успешный логин через POM выполнен!");
    }

    @Test
    @Story("Неуспешный вход в систему")
    @Severity(SeverityLevel.NORMAL)
    @Description("Проверка обработки заблокированного пользователя")
    @Owner("Alexey Michkov")
    void failedLoginTest() {
        loginPage.login("locked_out_user", "secret_sauce");

        String errorMessage = loginPage.getErrorMessage();
        assertThat(errorMessage).contains("Epic sadface: Sorry, this user has been locked out.");

        System.out.println("✅ Тест на заблокированного пользователя пройден!");
    }
}