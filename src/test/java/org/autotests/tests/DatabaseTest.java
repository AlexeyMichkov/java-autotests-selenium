package org.autotests.tests;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseTest {

    // Статический блок ВНУТРИ класса
    static {
                // Для Windows: явно указываем стратегию подключения
        System.setProperty("testcontainers.docker.client.strategy",
                "org.testcontainers.dockerclient.NpipeSocketClientProviderStrategy");

         //Отключаем Ryuk (может вызывать проблемы на Windows)
        System.setProperty("testcontainers.ryuk.disabled", "true");

        // Явно указываем Docker host для Windows
        System.setProperty("docker.host", "npipe:////./pipe/docker_engine");
    }

    private static Connection connection;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        // Используем H2 in-memory базу с режимом PostgreSQL
        String jdbcUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE";

        System.out.println("️  Используем H2 базу данных: " + jdbcUrl);

        connection = DriverManager.getConnection(jdbcUrl);

        // Создаём таблицу users
        createUsersTable();
    }

    private static void createUsersTable() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // ============================================
    // ТЕСТ 1: Вставка данных
    // ============================================

    @Test
    @Order(1)
    @DisplayName("Вставка нового пользователя")
    void testInsertUser() throws SQLException {
        String insertSQL = """
            INSERT INTO users (username, email)
            VALUES (?, ?)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, "john_doe");
            pstmt.setString(2, "john@example.com");
            int rowsAffected = pstmt.executeUpdate();

            assertThat(rowsAffected).isEqualTo(1);
        }
    }

    // ============================================
    // ТЕСТ 2: Чтение данных
    // ============================================

    @Test
    @Order(2)
    @DisplayName("Чтение пользователя из БД")
    void testSelectUser() throws SQLException {
        String selectSQL = """
            SELECT id, username, email, created_at
            FROM users
            WHERE username = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, "john_doe");

            try (ResultSet rs = pstmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString("username")).isEqualTo("john_doe");
                assertThat(rs.getString("email")).isEqualTo("john@example.com");
                assertThat(rs.getTimestamp("created_at")).isNotNull();
            }
        }
    }

    // ============================================
    // ТЕСТ 3: Обновление данных
    // ============================================

    @Test
    @Order(3)
    @DisplayName("Обновление email пользователя")
    void testUpdateUser() throws SQLException {
        String updateSQL = """
            UPDATE users
            SET email = ?
            WHERE username = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setString(1, "john.new@example.com");
            pstmt.setString(2, "john_doe");

            int rowsAffected = pstmt.executeUpdate();
            assertThat(rowsAffected).isEqualTo(1);
        }

        // Проверяем, что email обновился
        String selectSQL = "SELECT email FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, "john_doe");

            try (ResultSet rs = pstmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString("email")).isEqualTo("john.new@example.com");
            }
        }
    }

    // ============================================
    // ТЕСТ 4: Удаление данных
    // ============================================

    @Test
    @Order(4)
    @DisplayName("Удаление пользователя")
    void testDeleteUser() throws SQLException {
        String deleteSQL = "DELETE FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, "john_doe");

            int rowsAffected = pstmt.executeUpdate();
            assertThat(rowsAffected).isEqualTo(1);
        }

        // Проверяем, что пользователь удалён
        String selectSQL = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, "john_doe");

            try (ResultSet rs = pstmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt(1)).isEqualTo(0);
            }
        }
    }

    // ============================================
    // ТЕСТ 5: Проверка транзакций (откат)
    // ============================================

    @Test
    @Order(5)
    @DisplayName("Транзакция с откатом")
    void testTransactionRollback() throws SQLException {
        // Включаем ручной режим транзакций
        connection.setAutoCommit(false);

        try {
            // Вставляем пользователя
            String insertSQL = "INSERT INTO users (username, email) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setString(1, "temp_user");
                pstmt.setString(2, "temp@example.com");
                pstmt.executeUpdate();
            }

            // Делаем откат транзакции
            connection.rollback();

            // Проверяем, что пользователь НЕ сохранился
            String selectSQL = "SELECT COUNT(*) FROM users WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
                pstmt.setString(1, "temp_user");

                try (ResultSet rs = pstmt.executeQuery()) {
                    assertThat(rs.next()).isTrue();
                    assertThat(rs.getInt(1)).isEqualTo(0);
                }
            }

            // Возвращаем автокоммит
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            connection.rollback();
            connection.setAutoCommit(true);
            throw e;
        }
    }

    // ============================================
    // ТЕСТ 6: Проверка уникальности email
    // ============================================

    @Test
    @Order(6)
    @DisplayName("Нарушение уникальности email")
    void testUniqueEmailConstraint() throws SQLException {
        // Сначала вставляем пользователя
        String insertSQL = "INSERT INTO users (username, email) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, "user1");
            pstmt.setString(2, "unique@example.com");
            pstmt.executeUpdate();
        }

        // Пытаемся вставить второго пользователя с тем же email
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, "user2");
            pstmt.setString(2, "unique@example.com");

            // Должна выброситься ошибка
            assertThatThrownBy(() -> pstmt.executeUpdate())
                    .isInstanceOf(SQLException.class)
                    .hasMessageContaining("violation");  // или "Нарушение уникального"
        }
    }
}