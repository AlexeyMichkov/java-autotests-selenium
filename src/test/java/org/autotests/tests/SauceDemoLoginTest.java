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

@ExtendWith(ScreenshotExtension.class)
@Epic("E-commerce")
@Feature("Аутентификация")
public class SauceDemoLoginTest {

    WebDriver driver;
    LoginPage loginPage;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-data-dir=C:\\temp\\chrome-profile-" + System.currentTimeMillis());
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