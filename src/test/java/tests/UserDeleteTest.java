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

import java.util.HashMap;
import java.util.Map;

@Epic("Test cases for delete users")
@Feature("Delete user")
public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Try to delete user with ID = 2")
    @DisplayName("Delete user with ID=2")
    public void testDeleteUserID2(){

        //Login User
        Map<String,String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //Delete
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/2",
                        header,
                        cookie);

        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);

        //Check user data
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/2",
                        header,
                        cookie);


        Assertions.assertJsonHasField(responseUserData,"email");
        Assertions.assertJsonHasField(responseUserData,"username");
        Assertions.assertJsonHasField(responseUserData,"firstName");
        Assertions.assertJsonHasField(responseUserData,"lastName");
    }

    @Test
    @Description("Test case for successfully deleting the user")
    @DisplayName("Successfully delete the user")
    public void testDeleteUser(){
        //Create user
        Map<String, String> userData = DataGenerate.getRegistrationData();
        String userId = apiCoreRequests.createUser(userData);
        String email = userData.get("email");
        String password = userData.get("password");

        //Login
        Map<String,String> authData = new HashMap<>();
        authData.put("email",email);
        authData.put("password", password);
        Response responseGetAuthUser = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login",
                        authData
                );

        String header = this.getHeader(responseGetAuthUser, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuthUser, "auth_sid");

        //Delete user
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userId,
                        header,
                        cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);

        //Check user data
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                        header,
                        cookie);

        Assertions.assertResponseTextEquals(responseUserData, "User not found");
        Assertions.assertJsonHasNotField(responseUserData,"email");
        Assertions.assertJsonHasNotField(responseUserData,"username");
        Assertions.assertJsonHasNotField(responseUserData,"firstName");
        Assertions.assertJsonHasNotField(responseUserData,"lastName");
    }

    @Test
    @Description("Try to delete user using other user")
    @DisplayName("Delete other user")
    public void testDeleteByOtherUser(){
        //Generate User1
        Map<String, String> userData1 = DataGenerate.getRegistrationData();
        String userId1 = apiCoreRequests.createUser(userData1);

        //Generate User2
        Map<String, String> userData2 = DataGenerate.getRegistrationData();
        String userId2 = apiCoreRequests.createUser(userData2);

        //Login by user2
        Map<String,String> authData2 = new HashMap<>();
        authData2.put("email",userData2.get("email"));
        authData2.put("password", userData2.get("password"));
        Response responseGetAuthUser2 = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login",
                        authData2
                );

        //Delete user1 using user2
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userId1,
                        this.getHeader(responseGetAuthUser2, "x-csrf-token"),
                        this.getCookie(responseGetAuthUser2, "auth_sid")
                );

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);

        //Login by user1
        Map<String,String> authData1 = new HashMap<>();
        authData1.put("email",userData1.get("email"));
        authData1.put("password", userData2.get("password"));
        Response responseGetAuthUser1 = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login",
                        authData1
                );

        //Check that firstName isn't changed
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId1,
                        this.getHeader(responseGetAuthUser1, "x-csrf-token"),
                        this.getCookie(responseGetAuthUser1, "auth_sid"));

        Assertions.assertJsonHasField(responseUserData,"email");
        Assertions.assertJsonHasField(responseUserData,"username");
        Assertions.assertJsonHasField(responseUserData,"firstName");
        Assertions.assertJsonHasField(responseUserData,"lastName");
    }

}
