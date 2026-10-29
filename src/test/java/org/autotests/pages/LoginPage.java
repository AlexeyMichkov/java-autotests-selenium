package org.autotests.pages;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {

    private WebDriver driver;

    // Локаторы (если изменятся — правим только здесь!)
    private By usernameField = By.id("user-name");
    private By passwordField = By.id("password");
    private By loginButton = By.id("login-button");
    private By errorMessage = By.cssSelector("[data-test='error']");

    // Конструктор
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // Методы действий
    public void open() {
        driver.get("https://www.saucedemo.com/");
    }

    public void enterUsername(String username) {
        driver.findElement(usernameField).sendKeys(username);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clickLogin() {
        driver.findElement(loginButton).click();
    }

    public String getErrorMessage() {
        try {
            // Явное ожидание появления элемента (до 5 секунд)
            WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(5));
            WebElement errorElement = wait.until(
                    org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(errorMessage)
            );
            return errorElement.getText();
        } catch (Exception e) {
            System.err.println("⚠️ Не удалось найти элемент ошибки: " + e.getMessage());
            return ""; // Возвращаем пустую строку, если элемент не найден
        }
    }

    // Удобный метод "логин за один шаг"
    public void login(String username, String password) {
        open();
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }
}