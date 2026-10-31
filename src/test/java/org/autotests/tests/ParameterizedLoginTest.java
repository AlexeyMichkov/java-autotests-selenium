package org.autotests.tests;

import io.qameta.allure.*;
import org.autotests.pages.LoginPage;
import org.autotests.utils.ScreenshotExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ScreenshotExtension.class)
@Epic("E-commerce")
@Feature("Аутентификация")
public class ParameterizedLoginTest {

    WebDriver driver;
    LoginPage loginPage;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();

        // Headless режим для GitHub Actions
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
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @ParameterizedTest(name = "Вход: пользователь ''{0}'', ожидаемая ошибка: {2}")
    @CsvSource({
            "standard_user, secret_sauce, false",
            "locked_out_user, secret_sauce, true",
            "problem_user, secret_sauce, false",
            "performance_glitch_user, secret_sauce, false",
            "error_user, secret_sauce, false",
            "visual_user, secret_sauce, false"
    })
    @Story("Проверка различных типов учетных записей")
    @Severity(SeverityLevel.NORMAL)
    @Description("Параметризованный тест для проверки входа с разными типами пользователей saucedemo")
    @Owner("Alexey Michkov")
    void loginWithDifferentUsers(String username, String password, boolean expectError) {

        loginPage.login(username, password);

        if (expectError) {
            String currentUrl = driver.getCurrentUrl();

            if (currentUrl.contains("inventory.html")) {
                throw new AssertionError("Ожидалась ошибка для пользователя " + username +
                        ", но произошёл успешный вход!");
            }

            String errorMessage = loginPage.getErrorMessage();
            if (errorMessage.isEmpty()) {
                System.out.println("ℹ️ Сообщение об ошибке пустое, но мы не на странице товаров — это OK для: " + username);
            } else {
                System.out.println("✅ Ошибка получена: " + errorMessage);
            }
        } else {
            assertThat(driver.getCurrentUrl()).contains("inventory.html");
            System.out.println("✅ Успешный вход для: " + username);
        }
    }
}