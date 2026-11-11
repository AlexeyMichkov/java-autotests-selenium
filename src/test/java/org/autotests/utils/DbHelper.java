package org.autotests.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class DbHelper {
    private final String url;
    private final String username;
    private final String password;

    public DbHelper(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Выполняет SELECT запрос и возвращает количество строк (для COUNT)
     */
    public int getCount(String sqlQuery) {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения SQL запроса: " + sqlQuery, e);
        }
    }

    /**
     * Выполняет SELECT запрос и возвращает значение строки (для проверки конкретных данных)
     */
    public String getStringValue(String sqlQuery) {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery)) {

            if (rs.next()) {
                return rs.getString(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения SQL запроса: " + sqlQuery, e);
        }
    }

    /**
     * Выполняет INSERT, UPDATE, DELETE запросы
     */
    public void executeUpdate(String sqlQuery) {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sqlQuery);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления БД: " + sqlQuery, e);
        }
    }

    /**
     * Создает таблицу, если не существует (для подготовки тестовых данных)
     */
    public void createTableIfNotExists(String tableName, String columnsDefinition) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columnsDefinition + ")";
        executeUpdate(sql);
    }
}