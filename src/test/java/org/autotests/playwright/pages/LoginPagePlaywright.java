package org.autotests.playwright.pages;

import com.microsoft.playwright.Page;

public class LoginPagePlaywright {
    private final Page page;

    private final String USERNAME_INPUT = "#user-name";
    private final String PASSWORD_INPUT = "#password";
    private final String LOGIN_BUTTON = "#login-button";
    private final String ERROR_MESSAGE = "[data-test='error']";
    private final String INVENTORY_CONTAINER = "#inventory_container";

    public LoginPagePlaywright(Page page) {
        this.page = page;
    }

    public LoginPagePlaywright open() {
        page.navigate("https://www.saucedemo.com/");
        return this;
    }

    public LoginPagePlaywright enterUsername(String username) {
        page.fill(USERNAME_INPUT, username);
        return this;
    }

    public LoginPagePlaywright enterPassword(String password) {
        page.fill(PASSWORD_INPUT, password);
        return this;
    }

    public LoginPagePlaywright clickLogin() {
        page.click(LOGIN_BUTTON);
        return this;
    }

    // Ждем, пока URL изменится на страницу инвентаря (самый надежный способ)
    public LoginPagePlaywright login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();

        // Playwright автоматически подождет завершения навигации
        page.waitForURL("**/inventory.html");
        return this;
    }

    // Метод для негативных сценариев (не ждём перехода на inventory.html)
    public LoginPagePlaywright loginWithoutWait(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        // Не ждём навигации, сразу возвращаемся
        return this;
    }

    public String getErrorMessage() {
        return page.textContent(ERROR_MESSAGE);
    }

    public boolean isInventoryPageVisible() {
        // Берём первый найденный элемент
        return page.locator("#inventory_container").first().isVisible();
    }
}