package org.autotests.tests;

import org.junit.jupiter.api.extension.ExtendWith;
import org.autotests.utils.ScreenshotExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List; // ← Импорт для работы со списками элементов
import io.qameta.allure.*;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@ExtendWith(ScreenshotExtension.class)
@Epic("Обучение")
@Feature("Продвинутые локаторы")
@Execution(ExecutionMode.SAME_THREAD)
public class AdvancedLocatorsTest {

    WebDriver driver;

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
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();  // Закрываем браузер после каждого теста
        }
    }

    // Вспомогательный метод — логин (чтобы не дублировать код)
    private void login() {
        driver.get("https://www.saucedemo.com/");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
    }

    @Test
    @Story("Поиск товара по названию")
    @Severity(SeverityLevel.NORMAL)
    @Description("Поиск товара 'Sauce Labs Onesie' по названию и добавление в корзину")
    @Owner("Alexey Michkov")
    void advancedLocatorsTest() {

        // ============================================
        // ШАГ 1: Логинимся на сайте
        // ============================================
        login();

        // После логина мы на странице товаров
        String pageTitle = driver.findElement(By.className("title")).getText();
        assertThat(pageTitle).isEqualTo("Products");
        System.out.println("✅ Шаг 1: Залогинились, страница товаров загружена");


        // ============================================
        // ШАГ 2: Находим ВСЕ цены на странице
        // ============================================
        List<WebElement> allPrices = driver.findElements(By.cssSelector(".inventory_item_price"));

        System.out.println("Найдено цен на странице: " + allPrices.size());
        assertThat(allPrices.size()).isEqualTo(6);
        System.out.println("✅ Шаг 2: Найдено 6 цен");


        // ============================================
        // ШАГ 3: Проверяем первую цену
        // ============================================
        String firstPrice = allPrices.get(0).getText();
        System.out.println("Первая цена: " + firstPrice);

        assertThat(firstPrice).isEqualTo("$29.99");
        System.out.println("✅ Шаг 3: Первая цена = $29.99");


        // ============================================
        // ШАГ 4: Находим товар "Sauce Labs Onesie" ПО НАЗВАНИЮ
        // ============================================
        // Ищем ВСЕ элементы с классом inventory_item_name
        List<WebElement> allItemNames = driver.findElements(By.cssSelector(".inventory_item_name"));

        System.out.println("Найдено товаров: " + allItemNames.size());

        // Ищем среди них тот, что содержит текст "Sauce Labs Onesie"
        WebElement onesieItem = null;
        for (WebElement item : allItemNames) {
            String itemName = item.getText();
            System.out.println("Товар: " + itemName);

            if (itemName.contains("Sauce Labs Onesie")) {
                onesieItem = item;
                break;
            }
        }

        // Проверяем, что нашли товар
        assertThat(onesieItem).isNotNull();
        System.out.println("✅ Шаг 4: Товар 'Sauce Labs Onesie' найден");


        // ============================================
        // ШАГ 5: Находим родительский div этого товара
        // ============================================
        // Поднимаемся от названия товара к его контейнеру
        WebElement onesieContainer = onesieItem.findElement(By.xpath("./ancestor::div[@class='inventory_item']"));

        System.out.println("✅ Шаг 5: Найден контейнер товара");


        // ============================================
        // ШАГ 6: Добавляем товар в корзину
        // ============================================
        // Ищем кнопку "Add to cart" ВНУТРИ контейнера товара
        WebElement addButton = onesieContainer.findElement(By.cssSelector(".btn_primary.btn_inventory"));

        // Нажимаем на кнопку
        addButton.click();
        System.out.println("✅ Шаг 6: Кнопка 'Add to cart' нажата");


        // ============================================
        // ШАГ 7: Проверяем счётчик корзины
        // ============================================
        String cartBadge = driver.findElement(By.className("shopping_cart_badge")).getText();
        System.out.println("Счётчик корзины: " + cartBadge);

        assertThat(cartBadge).isEqualTo("1");
        System.out.println("✅ Шаг 7: Счётчик корзины = 1");
        System.out.println("\n ВСЕ ШАГИ ПРОЙДЕНЫ УСПЕШНО!");
    }
}