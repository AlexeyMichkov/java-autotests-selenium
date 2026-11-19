package org.autotests.tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import io.qameta.allure.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Epic("Обучение")
@Feature("Продвинутые локаторы")
@Execution(ExecutionMode.SAME_THREAD)
public class LocatorsPracticeTest extends BaseTest {   // <-- extends BaseTest

    @Test
    @Story("CSS-селекторы")
    @Severity(SeverityLevel.MINOR)
    @Description("Практика CSS-селекторов: поиск по ID, атрибутам, комбинациям")
    @Owner("Alexey Michkov")
    void cssSelectorsPractice() {
        driver.get("https://www.saucedemo.com/");

        WebElement usernameByCssId = driver.findElement(By.cssSelector("#user-name"));
        usernameByCssId.sendKeys("standard_user");
        usernameByCssId.clear();

        WebElement usernameByAttribute = driver.findElement(By.cssSelector("[placeholder='Username']"));
        usernameByAttribute.sendKeys("standard_user");
        usernameByAttribute.clear();

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

        WebElement loginByAttr = driver.findElement(By.xpath("//input[@id='login-button']"));
        assertThat(loginByAttr.isDisplayed()).isTrue();

        WebElement loginByTypeAttr = driver.findElement(By.xpath("//input[@type='submit' and @id='login-button']"));
        assertThat(loginByTypeAttr.isDisplayed()).isTrue();

        System.out.println("✅ XPath работает!");
    }

    @Test
    @Story("Поиск нескольких элементов")
    @Severity(SeverityLevel.NORMAL)
    @Description("Поиск всех кнопок 'Add to cart' и нажатие на третью")
    @Owner("Alexey Michkov")
    void findMultipleElements() {
        driver.get("https://www.saucedemo.com/");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        List<WebElement> allButtons = driver.findElements(By.cssSelector(".btn_primary.btn_inventory"));
        System.out.println("Найдено кнопок через CSS: " + allButtons.size());

        List<WebElement> allButtonsXpath = driver.findElements(By.xpath("//button[text()='Add to cart']"));
        System.out.println("Найдено кнопок через XPath: " + allButtonsXpath.size());

        assertThat(allButtonsXpath.size()).isEqualTo(6);

        allButtonsXpath.get(2).click();

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

        WebElement usernameInput = driver.findElement(By.xpath("//input[@placeholder='Username']"));
        usernameInput.sendKeys("standard_user");
        assertThat(usernameInput.getAttribute("id")).isEqualTo("user-name");

        WebElement parentDiv = driver.findElement(By.xpath("//input[@id='user-name']/.."));
        System.out.println("Родитель input — это: " + parentDiv.getTagName());
        System.out.println("Класс родителя: " + parentDiv.getAttribute("class"));

        List<WebElement> allInputs = driver.findElements(By.tagName("input"));
        System.out.println("Всего input элементов на странице: " + allInputs.size());

        WebElement loginButton = driver.findElement(By.xpath("//input[@type='submit']"));
        System.out.println("Кнопка login найдена: " + loginButton.isDisplayed());

        WebElement loginById = driver.findElement(By.id("login-button"));
        System.out.println("Тип кнопки: " + loginById.getAttribute("type"));
        System.out.println("Значение value: " + loginById.getAttribute("value"));

        System.out.println("✅ Навигация по DOM работает!");
    }
}