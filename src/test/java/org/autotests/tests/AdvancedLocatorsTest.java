package org.autotests.tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import io.qameta.allure.*;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Epic("Обучение")
@Feature("Продвинутые локаторы")
@Execution(ExecutionMode.SAME_THREAD)
public class AdvancedLocatorsTest extends BaseTest {   // <-- extends BaseTest

    // Вспомогательный метод — логин (driver теперь наследуется из BaseTest)
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

        login();

        String pageTitle = driver.findElement(By.className("title")).getText();
        assertThat(pageTitle).isEqualTo("Products");
        System.out.println("✅ Шаг 1: Залогинились, страница товаров загружена");

        List<WebElement> allPrices = driver.findElements(By.cssSelector(".inventory_item_price"));
        System.out.println("Найдено цен на странице: " + allPrices.size());
        assertThat(allPrices.size()).isEqualTo(6);
        System.out.println("✅ Шаг 2: Найдено 6 цен");

        String firstPrice = allPrices.get(0).getText();
        System.out.println("Первая цена: " + firstPrice);
        assertThat(firstPrice).isEqualTo("$29.99");
        System.out.println("✅ Шаг 3: Первая цена = $29.99");

        List<WebElement> allItemNames = driver.findElements(By.cssSelector(".inventory_item_name"));
        System.out.println("Найдено товаров: " + allItemNames.size());

        WebElement onesieItem = null;
        for (WebElement item : allItemNames) {
            String itemName = item.getText();
            System.out.println("Товар: " + itemName);
            if (itemName.contains("Sauce Labs Onesie")) {
                onesieItem = item;
                break;
            }
        }

        assertThat(onesieItem).isNotNull();
        System.out.println("✅ Шаг 4: Товар 'Sauce Labs Onesie' найден");

        WebElement onesieContainer = onesieItem.findElement(By.xpath("./ancestor::div[@class='inventory_item']"));
        System.out.println("✅ Шаг 5: Найден контейнер товара");

        WebElement addButton = onesieContainer.findElement(By.cssSelector(".btn_primary.btn_inventory"));
        addButton.click();
        System.out.println("✅ Шаг 6: Кнопка 'Add to cart' нажата");

        String cartBadge = driver.findElement(By.className("shopping_cart_badge")).getText();
        System.out.println("Счётчик корзины: " + cartBadge);
        assertThat(cartBadge).isEqualTo("1");
        System.out.println("✅ Шаг 7: Счётчик корзины = 1");
        System.out.println("\n ВСЕ ШАГИ ПРОЙДЕНЫ УСПЕШНО!");
    }
}