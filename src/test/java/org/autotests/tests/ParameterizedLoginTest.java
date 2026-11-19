package org.autotests.tests;

import io.qameta.allure.*;
import org.autotests.pages.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("E-commerce")
@Feature("Аутентификация")
public class ParameterizedLoginTest extends BaseTest {   // <-- extends BaseTest

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