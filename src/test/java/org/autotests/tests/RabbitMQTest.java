package org.autotests.tests;

import com.rabbitmq.client.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тест интеграции с RabbitMQ (локальный сервер).
 *
 * Проверяет:
 * 1. Создание очереди
 * 2. Отправку сообщения (Producer)
 * 3. Получение сообщения (Consumer)
 * 4. Корректность данных
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RabbitMQTest {

    private static Connection connection;
    private static Channel channel;

    /**
     * Инициализация: подключаемся к локальному RabbitMQ
     */
    @BeforeAll
    static void setUp() throws IOException, TimeoutException {
        // Создаём подключение к локальному RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        connection = factory.newConnection();
        channel = connection.createChannel();

        System.out.println("🐰 Подключение к локальному RabbitMQ установлено");
    }

    /**
     * Тест 1: Создание очереди
     */
    @Test
    @Order(1)
    @DisplayName("Создание очереди payments")
    void testCreateQueue() throws IOException {
        String queueName = "payments";
        channel.queueDeclare(queueName, false, false, false, null);
        System.out.println("✅ Очередь '" + queueName + "' создана");
    }

    /**
     * Тест 2: Отправка сообщения (Producer)
     */
    @Test
    @Order(2)
    @DisplayName("Отправка сообщения в очередь")
    void testSendMessage() throws IOException {
        String queueName = "payments";

        channel.queueDeclare(queueName, false, false, false, null);

        String message = """
            {
                "transactionId": "TXN-12345",
                "amount": 1000.50,
                "currency": "RUB",
                "from": "account-001",
                "to": "account-002"
            }
            """;

        channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println("📤 Сообщение отправлено");
    }

    /**
     * Тест 3: Получение сообщения (Consumer)
     */
    @Test
    @Order(3)
    @DisplayName("Получение сообщения из очереди")
    void testReceiveMessage() throws IOException {
        String queueName = "payments";

        GetResponse response = channel.basicGet(queueName, true);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();

        String receivedMessage = new String(response.getBody(), StandardCharsets.UTF_8);
        System.out.println("📥 Сообщение получено: " + receivedMessage);

        assertThat(receivedMessage).contains("TXN-12345");
        assertThat(receivedMessage).contains("1000.50");
        assertThat(receivedMessage).contains("RUB");
    }

    /**
     * Тест 4: Проверка нескольких сообщений
     */
    @Test
    @Order(4)
    @DisplayName("Обработка очереди из нескольких сообщений")
    void testMultipleMessages() throws IOException {
        String queueName = "notifications";

        channel.queueDeclare(queueName, false, false, false, null);

        for (int i = 1; i <= 3; i++) {
            String message = "Уведомление #" + i + ": Перевод выполнен";
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("📤 Отправлено: " + message);
        }

        for (int i = 1; i <= 3; i++) {
            GetResponse response = channel.basicGet(queueName, true);
            String receivedMessage = new String(response.getBody(), StandardCharsets.UTF_8);
            System.out.println("📥 Получено: " + receivedMessage);
            assertThat(receivedMessage).contains("Уведомление #" + i);
        }
    }

    /**
     * Завершение: закрываем подключение
     */
    @AfterAll
    static void tearDown() throws IOException, TimeoutException {
        if (channel != null) channel.close();
        if (connection != null) connection.close();
        System.out.println("🔴 Подключение к RabbitMQ закрыто");
    }
}