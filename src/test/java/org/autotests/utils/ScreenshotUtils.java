package org.autotests.utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

public class ScreenshotUtils {

    // Метод для создания скриншота и прикрепления к Allure
    public static void takeScreenshot(WebDriver driver, String testName) {
        try {
            // Делаем скриншот
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            // Прикрепляем к Allure отчёту
            Allure.addAttachment(
                    "Скриншот при падении теста: " + testName,  // Название
                    "image/png",                                  // Тип
                    new ByteArrayInputStream(screenshot),         // Данные
                    ".png"                                        // Расширение
            );

            System.out.println("✅ Скриншот сохранён для теста: " + testName);
        } catch (Exception e) {
            System.err.println("❌ Не удалось сделать скриншот: " + e.getMessage());
        }
    }
}