package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.OrderModel;

import java.util.List;
import java.util.Random;

import static data.EndpointAndUriData.*;
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;

public class OrderSteps {

    @Step("Получение хеш случайного ингредиента")
    public static String getRandomIngredientHash(){
        Response response =
                given()
                .log().all()
                .contentType(ContentType.JSON)
                .when()
                .get(DATA_INGREDIENT_GET)
                .then()
                .extract().response();

        if (response.statusCode() == HTTP_OK) {
            List<String> ingredientId = response.path("data._id");
                Random random = new Random();
                return ingredientId.get(random.nextInt(ingredientId.size()));
        }
        return null;
    }

    @Step("Создание заказа с авторизацией")
    public static Response createOrder(OrderModel order, String accessToken){
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .header("Authorization", accessToken)
                .post(CREATE_ORDER_POST)
                .then()
                .extract().response();
    }

    @Step("Создание заказа без авторизации")
    public static Response createOrder(OrderModel order){
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post(CREATE_ORDER_POST)
                .then()
                .extract().response();
    }
}
