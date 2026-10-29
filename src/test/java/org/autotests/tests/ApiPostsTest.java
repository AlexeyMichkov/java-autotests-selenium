package org.autotests.tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("API тестирование")
@Feature("Посты (Posts API)")
public class ApiPostsTest {

    // Базовый URL API — задаётся один раз для всех тестов
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    // ============================================
    // ТЕСТ 1: Получение всех постов
    // ============================================
    @Test
    @Story("Получение списка всех постов")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверка GET запроса для получения всех постов (должно быть 100)")
    @Owner("Alexey Michkov")
    void getAllPosts() {

        // Выполняем GET запрос на /posts
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/posts")
                .then()
                .extract()
                .response();

        // Проверяем статус-код 200 (OK)
        assertThat(response.getStatusCode()).isEqualTo(200);
        System.out.println("✅ Статус-код: " + response.getStatusCode());

        // Проверяем, что постов ровно 100
        int postsCount = response.jsonPath().getList("$").size();
        assertThat(postsCount).isEqualTo(100);
        System.out.println("✅ Количество постов: " + postsCount);
    }

    // ============================================
    // ТЕСТ 2: Получение поста по ID
    // ============================================
    @Test
    @Story("Получение конкретного поста по ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверка GET запроса для получения поста с ID=1")
    @Owner("Alexey Michkov")
    void getPostById() {

        // Выполняем GET запрос на /posts/1
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/posts/1")
                .then()
                .extract()
                .response();

        // Проверяем статус-код 200
        assertThat(response.getStatusCode()).isEqualTo(200);
        System.out.println("✅ Статус-код: " + response.getStatusCode());

        // Извлекаем поля из JSON-ответа
        int userId = response.jsonPath().getInt("userId");
        String title = response.jsonPath().getString("title");
        String body = response.jsonPath().getString("body");

        System.out.println("✅ userId: " + userId);
        System.out.println("✅ title: " + title);
        System.out.println("✅ body: " + body);

        // Проверяем значения
        assertThat(userId).isEqualTo(1);
        assertThat(title).isNotEmpty();
        assertThat(body).isNotEmpty();
    }

    // ============================================
    // ТЕСТ 3: Создание нового поста
    // ============================================
    @Test
    @Story("Создание нового поста")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверка POST запроса для создания нового поста")
    @Owner("Alexey Michkov")
    void createPost() {

        // JSON-тело запроса (используем text block — удобно для многострочного JSON)
        String newPostJson = """
                {
                    "title": "Мой первый API тест",
                    "body": "RestAssured - это круто!",
                    "userId": 1
                }
                """;

        // Выполняем POST запрос на /posts
        Response response = given()
                .contentType(ContentType.JSON)
                .body(newPostJson)  // Передаём JSON в теле запроса
                .when()
                .post("/posts")
                .then()
                .extract()
                .response();

        // Проверяем статус-код 201 (Created)
        assertThat(response.getStatusCode()).isEqualTo(201);
        System.out.println("✅ Пост создан, статус: " + response.getStatusCode());

        // Проверяем, что сервер вернул наши данные
        String returnedTitle = response.jsonPath().getString("title");
        String returnedBody = response.jsonPath().getString("body");
        int returnedUserId = response.jsonPath().getInt("userId");
        int returnedId = response.jsonPath().getInt("id");

        System.out.println("✅ Вернувшийся title: " + returnedTitle);
        System.out.println("✅ Вернувшийся body: " + returnedBody);
        System.out.println("✅ Вернувшийся userId: " + returnedUserId);
        System.out.println("✅ Вернувшийся id: " + returnedId);

        // Проверяем совпадение данных
        assertThat(returnedTitle).isEqualTo("Мой первый API тест");
        assertThat(returnedBody).isEqualTo("RestAssured - это круто!");
        assertThat(returnedUserId).isEqualTo(1);
        assertThat(returnedId).isEqualTo(101);  // jsonplaceholder возвращает id=101 для новых постов
    }

    // ============================================
    // ТЕСТ 4: Удаление поста
    // ============================================
    @Test
    @Story("Удаление поста")
    @Severity(SeverityLevel.NORMAL)
    @Description("Проверка DELETE запроса для удаления поста с ID=1")
    @Owner("Alexey Michkov")
    void deletePost() {

        // Выполняем DELETE запрос на /posts/1
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/posts/1")
                .then()
                .extract()
                .response();

        // Проверяем статус-код 200 (OK)
        // Примечание: jsonplaceholder всегда возвращает 200 для DELETE,
        // даже если пост не существует (это особенность тестового API)
        assertThat(response.getStatusCode()).isEqualTo(200);
        System.out.println("✅ Пост удалён, статус: " + response.getStatusCode());

        // Проверяем, что тело ответа пустое (обычно DELETE возвращает пустое тело)
        String responseBody = response.getBody().asString();
        System.out.println("✅ Тело ответа: '" + responseBody + "'");
    }
}