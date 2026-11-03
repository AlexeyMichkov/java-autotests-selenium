package org.autotests.playwright.tests;

import org.autotests.playwright.BasePlaywrightTest;
import org.autotests.playwright.pages.LoginPagePlaywright;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlaywrightLoginTest extends BasePlaywrightTest {

    private LoginPagePlaywright loginPage;

    @BeforeEach
    void setUp() {
        super.createContext();
        loginPage = new LoginPagePlaywright(page);
    }

    @AfterEach
    void tearDown() {
        super.closeContext();
    }

    // ============================================
    // ТЕСТ 1: Успешный логин с разными пользователями
    // ============================================

    @ParameterizedTest(name = "Логин пользователя: {0}")
    @Order(1)
    @DisplayName("Успешный логин с валидными credentials")
    @CsvSource({
            "standard_user, secret_sauce",
            "problem_user, secret_sauce",
            "performance_glitch_user, secret_sauce"
    })
    public void successfulLogin(String username, String password) {
        loginPage.open()
                .login(username, password);

        // Проверяем URL (более надёжно для problem_user)
        String currentUrl = page.url();
        assertThat(currentUrl)
                .as("Пользователь '%s' должен перейти на inventory.html", username)
                .contains("inventory.html");
    }

    // ============================================
    // ТЕСТ 2: Негативные сценарии логина
    // ============================================

    @ParameterizedTest(name = "Невалидный логин: username={0}, password={1}")
    @Order(2)
    @DisplayName("Логин с невалидными credentials должен показать ошибку")
    @CsvSource({
            "invalid_user, wrong_password",
            "locked_out_user, secret_sauce",
            "no_user, secret_sauce",       // Было: ", secret_sauce"
            "standard_user, no_pass",      // Было: "standard_user, "
            "no_user, no_pass"             // Было: ", "
    })
    public void invalidLogin(String username, String password) {
        loginPage.open()
                .loginWithoutWait(username, password);  // ← Не ждёт URL!

        String errorMessage = loginPage.getErrorMessage();

        assertThat(errorMessage)
                .as("Должна появиться ошибка при невалидном логине")
                .isNotNull()
                .isNotEmpty();
    }

    // ============================================
    // ТЕСТ 3: Валидация полей формы
    // ============================================

    @Test
    @Order(3)
    @DisplayName("Пустые поля должны показать ошибку")
    public void emptyFieldsValidation() {
        loginPage.open()
                .clickLogin();

        String errorMessage = loginPage.getErrorMessage();

        assertThat(errorMessage)
                .contains("Username is required");
    }

    // ============================================
    // ТЕСТ 4: Проверка URL после логина
    // ============================================

    @ParameterizedTest(name = "URL после логина пользователя: {0}")
    @Order(4)
    @DisplayName("После успешного логина URL должен измениться")
    @CsvSource({
            "standard_user, secret_sauce",
            "problem_user, secret_sauce"
    })
    public void urlAfterLogin(String username, String password) {
        loginPage.open()
                .login(username, password);

        String currentUrl = page.url();

        assertThat(currentUrl)
                .as("URL должен содержать 'inventory.html' после логина")
                .contains("inventory.html");
    }
}