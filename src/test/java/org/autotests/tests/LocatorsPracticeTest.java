package org.autotests.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import io.qameta.allure.*;

@Epic("Обучение")
@Feature("Продвинутые локаторы")
public class LocatorsPracticeTest {

    WebDriver driver;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-data-dir=C:\\temp\\chrome-profile-" + System.currentTimeMillis());
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-features=PasswordManager,PasswordManagerRedesign");
        options.addArguments("--disable-notifications");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Story("CSS-селекторы")
    @Severity(SeverityLevel.MINOR)
    @Description("Практика CSS-селекторов: поиск по ID, атрибутам, комбинациям")
    @Owner("Alexey Michkov")
    void cssSelectorsPractice() {
        // Открываем сайт
        driver.get("https://www.saucedemo.com/");

        // === РАЗНЫЕ СПОСОБЫ НАЙТИ ПОЛЕ ЛОГИНА ===

        // Способ 1: По ID (через CSS)
        WebElement usernameByCssId = driver.findElement(By.cssSelector("#user-name"));
        usernameByCssId.sendKeys("standard_user");
        usernameByCssId.clear();

        // Способ 2: По атрибуту (через CSS)
        WebElement usernameByAttribute = driver.findElement(By.cssSelector("[placeholder='Username']"));
        usernameByAttribute.sendKeys("standard_user");
        usernameByAttribute.clear();

        // Способ 3: По комбинации тег + атрибут
        WebElement usernameByTagAttr = driver.findElement(By.cssSelector("input#user-name"));
        usernameByTagAttr.sendKeys("standard_user");

        System.out.println("✅ CSS-селекторы работают!");
    }

    @Test
    @Story("XPath")
    @Severity(SeverityLevel.MINOR)
    @Description("Практика XPath: поиск по атрибутам, тексту")
    @Owner("Alexey Michkov")
    void xpathPractice() {
        driver.get("https://www.saucedemo.com/");

        // === РАЗНЫЕ СПОСОБЫ НАЙТИ КНОПКУ LOGIN ===

        // Способ 1: По атрибуту (XPath)
        WebElement loginByAttr = driver.findElement(By.xpath("//input[@id='login-button']"));
        assertThat(loginByAttr.isDisplayed()).isTrue();

        // Способ 2: По типу и атрибуту
        WebElement loginByTypeAttr = driver.findElement(By.xpath("//input[@type='submit' and @id='login-button']"));
        assertThat(loginByTypeAttr.isDisplayed()).isTrue();

        // Способ 3: По тексту (если бы текст был на кнопке)
        // WebElement loginByText = driver.findElement(By.xpath("//button[text()='Login']"));

        System.out.println("✅ XPath работает!");
    }

    @Test
    @Story("Поиск нескольких элементов")
    @Severity(SeverityLevel.NORMAL)
    @Description("Поиск всех кнопок 'Add to cart' и нажатие на третью")
    @Owner("Alexey Michkov")
    void findMultipleElements() {
        // Логинимся
        driver.get("https://www.saucedemo.com/");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // === НАХОДИМ ВСЕ КНОПКИ "Add to cart" ===

        // CSS-селектор: все кнопки с классом btn_primary
        List<WebElement> allButtons = driver.findElements(By.cssSelector(".btn_primary.btn_inventory"));
        System.out.println("Найдено кнопок через CSS: " + allButtons.size());

        // XPath: все кнопки с текстом "Add to cart"
        List<WebElement> allButtonsXpath = driver.findElements(By.xpath("//button[text()='Add to cart']"));
        System.out.println("Найдено кнопок через XPath: " + allButtonsXpath.size());

        // Проверяем, что их 6
        assertThat(allButtonsXpath.size()).isEqualTo(6);

        // === НАЖИМАЕМ НА ТРЕТЬЮ КНОПКУ (индекс в Java начинается с 0!) ===
        allButtonsXpath.get(2).click();

        // Проверяем счётчик корзины
        String badge = driver.findElement(By.className("shopping_cart_badge")).getText();
        assertThat(badge).isEqualTo("1");

        System.out.println("✅ Нажата третья кнопка, счётчик = " + badge);
    }

    @Test
    @Story("Навигация по DOM")
    @Severity(SeverityLevel.NORMAL)
    @Description("Навигация по DOM: родители, siblings, атрибуты")
    @Owner("Alexey Michkov")
    void xpathNavigation() {
        driver.get("https://www.saucedemo.com/");

        // === НАВИГАЦИЯ ПО DOM ===

        // 1. Найти input по placeholder (атрибуту)
        WebElement usernameInput = driver.findElement(
                By.xpath("//input[@placeholder='Username']")
        );
        usernameInput.sendKeys("standard_user");
        assertThat(usernameInput.getAttribute("id")).isEqualTo("user-name");

        // 2. Найти родитель элемента (div, в котором находится input)
        WebElement parentDiv = driver.findElement(
                By.xpath("//input[@id='user-name']/..")
        );
        System.out.println("Родитель input — это: " + parentDiv.getTagName());
        System.out.println("Класс родителя: " + parentDiv.getAttribute("class"));

        // 3. Найти все input элементы на странице
        List<WebElement> allInputs = driver.findElements(By.tagName("input"));
        System.out.println("Всего input элементов на странице: " + allInputs.size());

        // 4. Найти кнопку login через атрибут type
        WebElement loginButton = driver.findElement(
                By.xpath("//input[@type='submit']")
        );
        System.out.println("Кнопка login найдена: " + loginButton.isDisplayed());

        // 5. Альтернативный способ: найти по ID и проверить атрибут value
        WebElement loginById = driver.findElement(By.id("login-button"));
        System.out.println("Тип кнопки: " + loginById.getAttribute("type"));
        System.out.println("Значение value: " + loginById.getAttribute("value"));

        System.out.println("✅ Навигация по DOM работает!");
    }
}