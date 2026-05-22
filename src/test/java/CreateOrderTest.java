import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import model.LoginModel;
import model.OrderModel;
import model.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static steps.OrderSteps.createOrder;
import static steps.OrderSteps.getRandomIngredientHash;
import static steps.UserSteps.*;

public class CreateOrderTest extends BaseAPITest{
    private UserModel user;
    private LoginModel loginModel;
    private String accessToken;
    private String email;
    private String password;
    private String name;
    private List<String> ingredientsList;
    private OrderModel order;


    @Before
    public void generateData() {
        Faker faker = new Faker();
        email = faker.internet().emailAddress() + faker.regexify("[0-9]{4}");
        password = faker.regexify("[0-9]{4}");
        name = faker.name().firstName() + faker.regexify("[0-9]{4}");

        user = new UserModel(email, password, name);
        createUser(user);

        loginModel = new LoginModel(email, password);
        loginUser(loginModel);

        accessToken = getUserAccessToken(email, password);

        Random random = new Random();
        int randomCountIngredients = random.nextInt(5) + 1;

        ingredientsList = new ArrayList<>();
        for (int i = 0; i < randomCountIngredients; i++) {
            ingredientsList.add(getRandomIngredientHash());
        }
    }

    @Test
    @DisplayName("Проверка успешного создания заказа с ингредиентами с авторизацией")
    @Description("Тест для проверки возможности создания заказа с указанием ингредиентов при авторизации пользователя в системе")
    public void testCreateOrderWithIngredientsWithAuthSuccess() {

        order = new OrderModel(ingredientsList);
        createOrder(order, accessToken)
                .then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .extract().response();
    }

    @Test
    @DisplayName("Проверка успешного создания заказа с ингредиентами без авторизации")
    @Description("Тест для проверки возможности создания заказа с указанием ингредиентов неавторизованным пользователем")
    public void testCreateOrderWithIngredientsWithoutAuthSuccessSuccess() {

        order = new OrderModel(ingredientsList);
        createOrder(order)
                .then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .extract().response();
    }

    @Test
    @DisplayName("Проверка возникновения ошибки при попытке создания заказа без ингредиентов с авторизацией")
    @Description("Тест для проверки невозможности создания заказа БЕЗ указания ингредиентов при авторизации пользователя в системе")
    public void testCreateOrderWithoutIngredientsWithAuthFail() {

        order = new OrderModel(null);
        createOrder(order, accessToken)
                .then()
                .log().all()
                .statusCode(HTTP_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"))
                .extract().response();
    }

    @Test
    @DisplayName("Проверка возникновения ошибки при попытке создания заказа без ингредиентов без авторизации")
    @Description("Тест для проверки невозможности создания заказа БЕЗ указания ингредиентов неавторизованным пользователем")
    public void testCreateOrderWithoutIngredientsWithoutAuthFail() {

        order = new OrderModel(null);
        createOrder(order)
                .then()
                .log().all()
                .statusCode(HTTP_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"))
                .extract().response();
    }

    @Test
    @DisplayName("Проверка возникновения ошибки при попытке создания заказа с неверным хешем ингредиентов")
    @Description("Тест для проверки невозможности создания заказа с указанием ингредиентов авторизованным в системе пользователем при неверном хеше")
    public void testCreateOrderWithWrongHashAuthFail() {

        ingredientsList.add(getRandomIngredientHash() + System.currentTimeMillis());

        order = new OrderModel(ingredientsList);
        createOrder(order, accessToken)
                .then()
                .log().all()
                .statusCode(HTTP_INTERNAL_ERROR)
                .body((containsString("Internal Server Error")))
                .extract().response();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            deleteUser(accessToken);
        }
    }
}
