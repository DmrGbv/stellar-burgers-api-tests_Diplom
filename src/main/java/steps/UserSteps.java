package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.LoginModel;
import model.UserModel;

import static data.EndpointAndUriData.CREATE_USER_POST;
import static data.EndpointAndUriData.LOGIN_USER_POST;
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;

public class UserSteps {
    @Step("Создание пользователя")
    public static Response createUser(UserModel user){
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(CREATE_USER_POST)
                .then()
                .extract().response();
    }

    @Step("Авторизация пользователя с email {email} и паролем {password}")
    public static Response loginUser(LoginModel loginModel){
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(loginModel)
                .when()
                .post(LOGIN_USER_POST)
                .then()
                .extract().response();
    }

    @Step("Получение токена пользователя при авторизации")
    public static String getUserAccessToken(String email, String password) {
        LoginModel loginModel = new LoginModel(email, password);
        Response response = loginUser(loginModel);

        if (response.statusCode() == HTTP_OK) {
            return response.path("accessToken");
        }
        return null;
    }

    @Step("Удаление пользователя")
    public static Response deleteUser(String token) {
        return given()
                .log().all()
                .when()
                .header("Authorization", token)
                .delete("api/auth/user")
                .then()
                .extract().response();
    }
}