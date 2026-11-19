package org.autotests.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Self-check: доказывает, что скриншот в Allure реально прикладывается при падении.
 * Без этого теста "зелёный" mvn test НЕ подтверждает работу интеграции —
 * ведь на упавших тестах ветка скриншота не выполняется, а все UI-тесты проходят.
 *
 * Как проверить вручную:
 * 1. Уберите @Disabled ниже.
 * 2. mvn test -Dtest=FailureScreenshotSmokeTest
 * 3. mvn allure:serve  → откройте упавший тест, убедитесь, что attachment
 *    "Скриншот упавшего теста" присутствует.
 * 4. Верните @Disabled, чтобы self-check не ронял основной прогон в CI.
 */
class FailureScreenshotSmokeTest extends BaseTest {

    @Disabled("Self-check: временно включите для проверки скриншота в Allure")
    @Test
    void selfcheckShouldAttachScreenshotOnFailure() {
        driver.get("https://www.saucedemo.com");
        throw new RuntimeException("selfcheck: намеренное падение для проверки скриншота");
    }
}