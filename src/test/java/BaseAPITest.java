import io.restassured.RestAssured;

import org.junit.BeforeClass;

import static data.EndpointAndUriData.*;

public class BaseAPITest  {
    @BeforeClass
    public static void setUp(){
        RestAssured.baseURI = BASE_URI;
    }
}
