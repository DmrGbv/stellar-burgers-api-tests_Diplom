import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.equalTo;
import static steps.UserSteps.*;

public class CreateUserTest extends BaseAPITest{
    private UserModel user;
    private String email;
    private String password;
    private String name;
    private String accessToken;

    @Before
    public void generateData() {
        Faker faker = new Faker();
        email = faker.internet().emailAddress() + faker.regexify("[0-9]{4}");
        password = faker.regexify("[0-9]{4}");
        name = faker.name().firstName() + faker.regexify("[0-9]{4}");
    }

    @Test
    @DisplayName("Проверка успешного создания уникального пользователя")
    @Description("Тест для проверки возможности создания уникального пользователя при передачи в ручку всех обязательных полей и получения правильного кода ответа")
    public void testCreateUniqueUserSuccess() {
        user = new UserModel(email, password, name);

        createUser(user)
                .then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .extract().response();

    }

    @Test
    @DisplayName("Проверка возникновения ошибки при попытке создания двух одинаковых пользователей")
    @Description("Тест для проверки НЕвозможности создания уже зарегистрированного пользователя")
    public void testCreateDuplicateUserFail() {
        this.user = new UserModel(email, password, name);

        createUser(this.user);

        createUser(this.user)
                .then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"))
                .extract().response();
    }

    @Test
    @DisplayName("Проверка возникновения ошибки при попытке создания пользователя без заполнения обязательного поля Email")
    @Description("Тест для проверки НЕвозможности создания пользователя при НЕ заполнении обязательного поля Email")
    public void testCreateUserWithoutEmailFail() {
        user = new UserModel(null, password, name);

        createUser(user)
                .then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .extract().response();
    }

    @Test
    @DisplayName("Проверка возникновения ошибки при попытке создания пользователя без заполнения обязательного поля Пароль")
    @Description("Тест для проверки НЕвозможности создания пользователя при НЕ заполнении обязательного поля Пароль")
    public void testCreateUserWithoutPasswordFail() {
        user = new UserModel(email, null, name);

        createUser(user)
                .then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .extract().response();
    }

    @Test
    @DisplayName("Проверка возникновения ошибки при попытке создания пользователя без заполнения обязательного поля Имя")
    @Description("Тест для проверки НЕвозможности создания пользователя при НЕ заполнении обязательного поля Имя")
    public void testCreateUserWithoutNameFail() {
        user = new UserModel(email, password, null);

        createUser(user)
                .then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .extract().response();
    }

    @Test
    @DisplayName("Проверка возникновения ошибки при попытке создания пользователя без заполнения обязательных полей")
    @Description("Тест для проверки НЕвозможности создания пользователя при НЕ заполнении обязательных полей Email, Пароль и Имя")
    public void testCreateUserWithoutDataFail() {
        user = new UserModel(null, null, null);

        createUser(user)
                .then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .extract().response();
    }

    @After
    public void tearDown() {
        String accessToken = getUserAccessToken(email, password);
        System.out.println(accessToken);
        if (accessToken != null) {
            deleteUser(accessToken);
        }
    }
}
