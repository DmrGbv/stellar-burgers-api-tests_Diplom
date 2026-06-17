import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import model.LoginModel;
import model.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static steps.UserSteps.*;

public class LoginUserTest extends BaseAPITest {
    private UserModel user;
    private String email;
    private String password;
    private String name;

    @Before
    public void generateData() {
        Faker faker = new Faker();
        email = faker.internet().emailAddress() + faker.regexify("[0-9]{4}");
        password = faker.regexify("[0-9]{4}");
        name = faker.name().firstName() + faker.regexify("[0-9]{4}");

        user = new UserModel(email, password, name);
        createUser(user);
    }

    @Test
    @DisplayName("Проверка успешной авторизации под существующим пользователем")
    @Description("Тест для проверки успешной авторизации под существующим пользователем при заполнении обязательных полей Email и Пароль")
    public void testExistUserLoginSuccess() {
        LoginModel loginModel = new LoginModel(email, password);
        loginUser(loginModel)
                .then().log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
    }

    @Test
    @DisplayName("Проверка возникновения ошибки при попытке авторизации с несуществующими Email и Паролем")
    @Description("Тест для проверки невозможности авторизации при вводе несуществующих данных в поля Email и Пароль")
    public void testUserLoginWithWrongEmailAndPasswordFail() {
        LoginModel loginModel = new LoginModel(email + System.currentTimeMillis(), password + System.currentTimeMillis());
        loginUser(loginModel)
                .then().log().all()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Проверка возникновения ошибки при попытке авторизации с несуществующим Email")
    @Description("Тест для проверки невозможности авторизации при вводе несуществующих данных в поле Email и существующими данными в поле Пароль")
    public void testUserLoginWithWrongEmailFail() {
        LoginModel loginModel = new LoginModel(email + System.currentTimeMillis(), password);
        loginUser(loginModel)
                .then().log().all()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Проверка возникновения ошибки при попытке авторизации с несуществующим Паролем")
    @Description("Тест для проверки невозможности авторизации при вводе несуществующих данных в поле Пароль и существующими данными в поле Email")
    public void testUserLoginWithWrongPasswordFail() {
        LoginModel loginModel = new LoginModel(email, password + System.currentTimeMillis());
        loginUser(loginModel)
                .then().log().all()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }


    @After
    public void tearDown() {
        String accessToken = getUserAccessToken(email, password);
        if (accessToken != null) {
            deleteUser(accessToken);
        }
    }

}
