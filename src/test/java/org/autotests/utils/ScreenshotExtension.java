package org.autotests.utils;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;

public class ScreenshotExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        // Проверяем, упал ли тест
        boolean testFailed = context.getExecutionException().isPresent();

        if (testFailed) {
            // Получаем имя теста
            String testName = context.getRequiredTestMethod().getName();
            System.out.println("⚠️ Тест упал: " + testName);

            // Пытаемся получить driver из тестового класса
            Object testInstance = context.getRequiredTestInstance();
            WebDriver driver = getDriverFromTestInstance(testInstance);

            if (driver != null) {
                // Делаем скриншот
                ScreenshotUtils.takeScreenshot(driver, testName);
            } else {
                System.err.println("❌ Не удалось получить WebDriver из теста");
            }
        }
    }

    // Метод для получения driver из тестового класса через рефлексию
    private WebDriver getDriverFromTestInstance(Object testInstance) {
        try {
            // Ищем поле с именем "driver" в тестовом классе
            Field driverField = testInstance.getClass().getDeclaredField("driver");
            driverField.setAccessible(true);
            return (WebDriver) driverField.get(testInstance);
        } catch (Exception e) {
            return null;
        }
    }
}