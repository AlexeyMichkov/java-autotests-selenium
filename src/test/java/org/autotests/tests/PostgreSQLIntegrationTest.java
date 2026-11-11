package org.autotests.tests;

import org.autotests.utils.DbHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Гарантируем порядок выполнения
public class PostgreSQLIntegrationTest {
    static {
        // Явно указываем использовать Docker Desktop на Windows
        System.setProperty("docker.client.strategy",
                "org.testcontainers.dockerclient.NpipeSocketClientProviderStrategy");
    }
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static DbHelper dbHelper;

    @BeforeAll
    static void setUp() {
        dbHelper = new DbHelper(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        dbHelper.createTableIfNotExists(
                "users",
                "id SERIAL PRIMARY KEY, " +
                        "first_name VARCHAR(50), " +
                        "last_name VARCHAR(50), " +
                        "email VARCHAR(100)"
        );

        System.out.println("✅ PostgreSQL запущен: " + postgres.getJdbcUrl());
    }

    @Test
    @Order(1) // Выполнится первым
    public void testUserInsertAndVerifyViaSQL() {
        System.out.println("🔍 Тест 1: проверка вставки и чтения из БД");

        int countBefore = dbHelper.getCount("SELECT COUNT(*) FROM users");
        System.out.println("📊 Пользователей до теста: " + countBefore);
        assertEquals(0, countBefore, "Таблица users должна быть пуста перед тестом");

        dbHelper.executeUpdate(
                "INSERT INTO users (first_name, last_name, email) " +
                        "VALUES ('Alexey', 'Michkov', 'alexey.michkov@test.com')"
        );
        System.out.println("✅ Пользователь добавлен в БД");

        int countAfter = dbHelper.getCount("SELECT COUNT(*) FROM users");
        assertEquals(1, countAfter, "В таблице должен быть ровно 1 пользователь");

        String lastName = dbHelper.getStringValue(
                "SELECT last_name FROM users WHERE email = 'alexey.michkov@test.com'"
        );
        assertNotNull(lastName, "Фамилия пользователя не должна быть null");
        assertEquals("Michkov", lastName, "Фамилия должна совпадать");

        System.out.println("✅ Все проверки пройдены! Данные корректно сохранены в БД.");
    }

    @Test
    @Order(2) // Выполнится вторым
    public void testMultipleUsersAndAggregation() {
        System.out.println("🔍 Тест 2: проверка агрегатных функций");

        dbHelper.executeUpdate(
                "INSERT INTO users (first_name, last_name, email) VALUES " +
                        "('John', 'Doe', 'john@test.com'), " +
                        "('Jane', 'Smith', 'jane@test.com'), " +
                        "('Bob', 'Johnson', 'bob@test.com')"
        );

        int totalCount = dbHelper.getCount("SELECT COUNT(*) FROM users");
        System.out.println("📊 Всего пользователей: " + totalCount);
        assertEquals(4, totalCount, "Должно быть 4 пользователя (1 из первого теста + 3 новых)");

        int jCount = dbHelper.getCount(
                "SELECT COUNT(*) FROM users WHERE last_name LIKE 'J%'"
        );
        System.out.println("📊 Пользователей на J: " + jCount);
        assertEquals(2, jCount, "Должно быть 2 пользователя с фамилией на J (Johnson и Jane)");

        int uniqueEmails = dbHelper.getCount("SELECT COUNT(DISTINCT email) FROM users");
        assertEquals(totalCount, uniqueEmails, "Все email должны быть уникальны");

        System.out.println("✅ Агрегатные проверки пройдены!");
    }
}