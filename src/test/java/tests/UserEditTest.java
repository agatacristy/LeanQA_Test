package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Edit different user data")
@Feature("Edit user")
public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test successfully change firstName for the user")
    @DisplayName("Change firstName")
    @Severity(SeverityLevel.CRITICAL)
    public void testEditJustCreatedTest(){
        //Generate User
        Map<String, String> userData = DataGenerate.getRegistrationData();
        String userId = apiCoreRequests.createUser(userData);

        //Login User
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //Edit
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

        //Get
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Description("Try to change user data without authentication")
    @DisplayName("Edit data without authentication")
    @Severity(SeverityLevel.TRIVIAL)
    public void testEditWithoutAuth(){
        Map<String, String> userData = DataGenerate.getRegistrationData();
        String userId = apiCoreRequests.createUser(userData);

        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                        editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
    }

    @Test
    @Description("Try to change data for other user")
    @DisplayName("Change data for other user")
    @Severity(SeverityLevel.TRIVIAL)
    public void testEditByOtherUser(){
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

        //Edit data for user1 using user2
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/" + userId1,
                        this.getHeader(responseGetAuthUser2, "x-csrf-token"),
                        this.getCookie(responseGetAuthUser2, "auth_sid"),
                        editData);
        Assertions.assertResponseCodeEquals(responseEditUser, 200);

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

        Assertions.assertJsonByNameNotEqual(responseUserData, "firstName", newName);
    }

    @Test
    @Description("Change email to the email without @")
    @DisplayName("Email with incorrect value")
    @Severity(SeverityLevel.NORMAL)
    public void testEditIncorrectEmail(){
        //Generate User
        Map<String, String> userData = DataGenerate.getRegistrationData();
        String userId = apiCoreRequests.createUser(userData);
        String email = userData.get("email");

        //Login User
        Map<String,String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //Edit
        String newEmail = email.replace("@", "");
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);
        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");
        Assertions.assertResponseCodeEquals(responseEditUser, 400);

        //Get
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByNameNotEqual(responseUserData, "email", newEmail);
    }

    @Test
    @Description("Change firstName to the very short firstName")
    @DisplayName("Very short firstName")
    @Severity(SeverityLevel.NORMAL)
    public void testEditShortFirstName(){
        //Generate User
        Map<String, String> userData = DataGenerate.getRegistrationData();
        String userId = apiCoreRequests.createUser(userData);

        //Login User
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //Edit
        String newName = DataGenerate.getString(1);

        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

        Assertions.assertResponseTextContains(responseEditUser, "Too short value for field firstName");
        Assertions.assertResponseCodeEquals(responseEditUser, 400);

        //Get
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByNameNotEqual(responseUserData, "firstName", newName);
    }

}
