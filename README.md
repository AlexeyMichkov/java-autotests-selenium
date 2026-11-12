# 🤖 Java AutoTests - Selenium + Playwright + API Testing

Автоматизированный фреймворк для тестирования веб-приложений и API, разработанный на Java. Проект демонстрирует современные подходы к автоматизации тестирования, включая UI-тесты, API-тесты, работу с базами данных и интеграцию с CI/CD.

## 🛠 Технологии и стек

### UI Тестирование
- **Selenium WebDriver 4.27.0** — автоматизация браузера
- **Playwright 1.40.0** — современная альтернатива Selenium
- **Page Object Model (POM)** — паттерн проектирования для организации тестов

### API Тестирование
- **RestAssured 5.4.0** — тестирование REST API
- **Jackson 2.17.0** — работа с JSON

### Базы данных
- **H2 Database 2.3.232** — in-memory база для быстрых тестов
- **PostgreSQL** — через Testcontainers для интеграционных тестов
- **JDBC** — прямое выполнение SQL-запросов из тестов

### Инфраструктура
- **Testcontainers 1.21.3** — изолированные Docker-контейнеры (PostgreSQL, RabbitMQ)
- **Docker** — контейнеризация тестовых окружений
- **RabbitMQ** — тестирование асинхронных сообщений

### Отчётность и CI/CD
- **Allure 2.25.0** — красивые отчёты о тестах
- **JUnit 5** — фреймворк для тестов
- **AssertJ** — удобные ассерты
- **GitHub Actions** — автоматический запуск тестов в CI/CD

## 📁 Структура проекта

*📂 pages/** — Page Object классы для Selenium
- CartPage.java
- CheckoutPage.java  
- InventoryPage.java
- LoginPage.java

**📂 playwright/** — Playwright тесты
- pages/LoginPagePlaywright.java
- tests/PlaywrightLoginTest.java

**📂 tests/** — Тестовые классы
- AdvancedLocatorsTest.java
- ApiPostsTest.java
- CartTest.java
- DatabaseTest.java
- H2DatabaseTest.java ✅
- PostgreSQLIntegrationTest.java ✅
- RabbitMQTest.java ✅
- ParameterizedLoginTest.java

**📂 utils/** — Утилиты
- DbHelper.java ✅ (SQL-помощник для работы с БД)
- ScreenshotExtension.java
- ScreenshotUtils.java

## 🚀 Как запустить тесты

### Предварительные требования
- Java 17+
- Maven 3.6+
- Docker Desktop (для Testcontainers тестов)

### Запуск всех тестов
```bash
mvn clean test

**Запуск в headless режиме (без окна браузера)**
mvn test -P headless

**Запуск только H2 Database тестов**
mvn test -Dtest=H2DatabaseTest

**Генерация Allure отчёта**
mvn allure:serve

✨ Ключевые особенности
🗄 Database Verification Pattern
Проект демонстрирует паттерн проверки данных напрямую в базе данных:
// Пример: проверка через SQL после действия в приложении
int countAfter = dbHelper.getCount("SELECT COUNT(*) FROM users");
assertEquals(1, countAfter, "Пользователь должен быть сохранён в БД");

Преимущества:
Проверка целостности данных на уровне БД, а не только UI/API
Ловит баги, когда API возвращает 200 OK, но данные не сохранились
Поддержка H2 (быстрые unit-тесты) и PostgreSQL (интеграционные тесты)

🧪 Изоляция тестов
Каждый тест работает с уникальной базой данных, что гарантирует независимость тестов друг от друга.

📊 Allure отчёты
 Красивые интерактивные отчёты с:
- Скриншотами при падении тестов
- Логами запросов и ответов
- Параметризацией тестов
- Epic/Feature/Story разметкой

🔄 CI/CD интеграция
Автоматический запуск тестов при каждом push через GitHub Actions с публикацией Allure отчётов на GitHub Pages.

📚 Примеры тестов
- H2 Database Test:

@Test
public void testInsertAndVerifyWithSQL() {
    // Вставка данных
    dbHelper.executeUpdate(
        "INSERT INTO users (first_name, last_name, email) " +
        "VALUES ('Alexey', 'Michkov', 'alexey@test.com')"
    );
    
    // Проверка через SQL
    int count = dbHelper.getCount("SELECT COUNT(*) FROM users");
    assertEquals(1, count);
}

- API Test с RestAssured:

@Test
public void testCreatePost() {
    given()
        .contentType("application/json")
        .body("{\"title\": \"Test\", \"body\": \"Content\", \"userId\": 1}")
    .when()
        .post("/posts")
    .then()
        .statusCode(201);
}

🎯 Цель проекта
- Этот проект создан для:
- Демонстрации навыков автоматизации тестирования
- Изучения современных инструментов (Playwright, Testcontainers, RestAssured)
- Практики паттернов проектирования (Page Object Model)
- Интеграции с CI/CD и системами отчётности

👨‍💻 Автор
Алексей Мичков
Email: [michkov.imperial@gmail.com]
LinkedIn: [https://www.linkedin.com/in/%D0%B0%D0%BB%D0%B5%D0%BA%D1%81%D0%B5%D0%B9-%D0%BC%D0%B8%D1%87%D0%BA%D0%BE%D0%B2-4b9056252/]
Резюме: https://perm.hh.ru/resume/d97d56c0ff0cba3e400039ed1f786f62555047

📄 Лицензия
MIT License
