package org.autotests.tests;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.ByteArrayInputStream;

/**
 * Базовый класс для всех Selenium-тестов.
 *
 * Устраняет дублирование: раньше каждый тестовый класс содержал
 * собственную копию настройки ChromeOptions, создания WebDriver
 * и метода tearDown (~50 строк на класс). Теперь вся логика — здесь.
 *
 * Интеграция скриншотов с Allure реализована через
 * TestExecutionExceptionHandler (шаг 10 lifecycle JUnit5), который
 * ГАРАНТИРОВАННО выполняется до @AfterEach (шаг 12) и пока браузер ещё жив.
 * Это надёжнее, чем TestWatcher: у TestWatcher порядок относительно
 * @AfterEach не гарантирован, и на практике он срабатывает ПОСЛЕ quit().
 */
@ExtendWith(BaseTest.FailureScreenshotTracker.class)
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    void setUpDriver() {
        ChromeOptions options = new ChromeOptions();

        boolean isHeaded = Boolean.parseBoolean(System.getProperty("headed", "false"));
        if (!isHeaded) {
            options.addArguments("--headless=new");
            System.out.println("🔹 Запуск в HEADLESS режиме");
        } else {
            System.out.println("🔹 Запуск в HEADED режиме");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-features=PasswordManager,PasswordManagerRedesign");
        options.addArguments("--disable-notifications");

        driver = new ChromeDriver(options);
    }

    @AfterEach
    void tearDownDriver() {
        // Скриншот в Allure делает FailureScreenshotTracker до этого момента.
        if (driver != null) {
            driver.quit();
        }
    }

    private void attachFailureScreenshotToAllure() {
        try {
            if (driver instanceof TakesScreenshot screenshotter) {
                byte[] png = screenshotter.getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Скриншот упавшего теста", "image/png",
                        new ByteArrayInputStream(png), ".png");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Не удалось прикрепить скриншот к Allure: " + e.getMessage());
        }
    }

    /**
     * Расширение-обработчик падения теста. Срабатывает на шаге 10 lifecycle
     * (до @AfterEach), делает скриншот в Allure и ОБЯЗАТЕЛЬНО перебрасывает
     * исключение дальше — иначе JUnit сочтёт тест зелёным.
     */
    static class FailureScreenshotTracker implements TestExecutionExceptionHandler {
        @Override
        public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
                throws Throwable {
            findBaseTest(context.getRequiredTestInstance()).attachFailureScreenshotToAllure();
            throw throwable;
        }

        private BaseTest findBaseTest(Object testInstance) {
            for (Class<?> c = testInstance.getClass(); c != null; c = c.getSuperclass()) {
                if (c == BaseTest.class) {
                    return (BaseTest) testInstance;
                }
            }
            throw new IllegalStateException("Тест " + testInstance + " не наследует BaseTest");
        }
    }
}