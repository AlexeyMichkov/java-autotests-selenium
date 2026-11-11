package org.autotests.tests;

import org.autotests.utils.DbHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тест с использованием H2 in-memory базы данных.
 * Не требует Docker - работает полностью в памяти Java.
 */
public class H2DatabaseTest {

    private DbHelper dbHelper;

    @BeforeEach
    void setUp() throws SQLException {
        // Уникальная база для каждого теста
        String url = "jdbc:h2:mem:testdb_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1";
        String username = "sa";
        String password = "";

        dbHelper = new DbHelper(url, username, password);

        // Создаем таблицу пользователей
        dbHelper.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "first_name VARCHAR(50), " +
                        "last_name VARCHAR(50), " +
                        "email VARCHAR(100))"
        );

        System.out.println("✅ H2 база данных подключена");
    }

    @Test
    public void testInsertAndVerifyWithSQL() {
        System.out.println(" Тест 1: Проверка вставки данных через SQL");

        // Очищаем таблицу перед тестом
        dbHelper.executeUpdate("DELETE FROM users");

        // Проверяем, что таблица пуста
        int countBefore = dbHelper.getCount("SELECT COUNT(*) FROM users");
        assertEquals(0, countBefore);

        // Вставляем пользователя
        dbHelper.executeUpdate(
                "INSERT INTO users (first_name, last_name, email) " +
                        "VALUES ('Alexey', 'Michkov', 'alexey@test.com')"
        );

        // Проверяем через SQL, что данные в базе
        int countAfter = dbHelper.getCount("SELECT COUNT(*) FROM users");
        assertEquals(1, countAfter);

        // Проверяем конкретные данные
        String lastName = dbHelper.getStringValue(
                "SELECT last_name FROM users WHERE email = 'alexey@test.com'"
        );
        assertNotNull(lastName);
        assertEquals("Michkov", lastName);

        System.out.println("✅ Данные успешно проверены через SQL-запросы!");
    }

    @Test
    public void testAggregationFunctions() {
        System.out.println(" Тест 2: Проверка агрегатных функций");

        // Очищаем и добавляем тестовые данные
        dbHelper.executeUpdate("DELETE FROM users");
        dbHelper.executeUpdate(
                "INSERT INTO users (first_name, last_name, email) VALUES " +
                        "('John', 'Doe', 'john@test.com'), " +
                        "('Jane', 'Smith', 'jane@test.com'), " +
                        "('Bob', 'Johnson', 'bob@test.com'), " +
                        "('Alice', 'Johnson', 'alice@test.com')"
        );

        // COUNT
        int totalCount = dbHelper.getCount("SELECT COUNT(*) FROM users");
        System.out.println(" Всего пользователей: " + totalCount);
        assertEquals(4, totalCount);

        // COUNT с WHERE
        int johnsonCount = dbHelper.getCount(
                "SELECT COUNT(*) FROM users WHERE last_name = 'Johnson'"
        );
        System.out.println(" Фамилия Johnson: " + johnsonCount);
        assertEquals(2, johnsonCount);

        // DISTINCT
        int uniqueLastNames = dbHelper.getCount(
                "SELECT COUNT(DISTINCT last_name) FROM users"
        );
        System.out.println(" Уникальных фамилий: " + uniqueLastNames);
        assertEquals(3, uniqueLastNames); // Doe, Smith, Johnson

        System.out.println("✅ Агрегатные функции работают корректно!");
    }
}