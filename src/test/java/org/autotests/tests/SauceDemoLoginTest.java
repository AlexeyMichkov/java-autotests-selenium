package org.autotests.tests;

import io.qameta.allure.*;
import org.autotests.pages.LoginPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("E-commerce")
@Feature("Аутентификация")
@Execution(ExecutionMode.SAME_THREAD)
public class SauceDemoLoginTest extends BaseTest {   // <-- extends BaseTest

    LoginPage loginPage;

    /**
     * driver уже создан в BaseTest.setUpDriver() (родительский @BeforeEach
     * выполняется раньше дочернего), поэтому здесь остаётся только
     * инициализация Page Object.
     */
    @BeforeEach
    void setUpPages() {
        loginPage = new LoginPage(driver);
    }

    @Test
    @Story("Успешный вход в систему")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверка успешного логина с валидными учётными данными")
    @Owner("Alexey Michkov")
    void successfulLoginTest() {
        loginPage.login("standard_user", "secret_sauce");

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