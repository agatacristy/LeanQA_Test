package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("User registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Try to create user with existing email")
    @DisplayName("Existing email")
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";
        Map<String,String> userData = new HashMap<>();
        userData.put("email",email);
        userData = DataGenerate.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
    }

    @Test
    @Description("This test successfully creats the user")
    @DisplayName("Successfully creation")
    public void testCreateUserSuccessfully(){
        String email = DataGenerate.getRandomEmail();
        Map<String,String> userData = DataGenerate.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Description("Try to create a user with incorrect email")
    @DisplayName("Incorrect email")
    public void testCreateUserWithIncorrectEmail(){
        String email = DataGenerate.getRandomEmail();
        Map<String,String> data = new HashMap<>();
        data.put("email","learnqaexample.com");
        Map<String,String> userData = DataGenerate.getRegistrationData(data);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @Description("Try to create a user when one of the required parameters are absent")
    @DisplayName("Without one of the parameter")
    @ParameterizedTest
    @ValueSource(strings = {"email","password", "username", "firstName", "lastName"})
    public void testCreateUserWithParameter(String parameter){
        String email = DataGenerate.getRandomEmail();
        Map<String,String> userData = DataGenerate.getRegistrationDataWithoutParameter(parameter);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + parameter);
    }

    @Test
    @Description("Try to create a user with very short username")
    @DisplayName("Very short username")
    public void testCreateUserWithShortUserName(){
        String email = DataGenerate.getRandomEmail();
        Map<String,String> data = new HashMap<>();
        data.put("username",DataGenerate.getString(1));
        Map<String,String> userData = DataGenerate.getRegistrationData(data);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
    }

    @Test
    @Description("Try to create a user with very long username")
    @DisplayName("Very long username")
    public void testCreateUserWithLongUserName(){
        String email = DataGenerate.getRandomEmail();
        Map<String,String> data = new HashMap<>();
        data.put("username",DataGenerate.getString(251));
        Map<String,String> userData = DataGenerate.getRegistrationData(data);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
    }
}
