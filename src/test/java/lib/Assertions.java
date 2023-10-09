package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Assertions {
    public static void assertJsonByName(Response Response, String name, int expectedValue){
        Response.then().assertThat().body("$",hasKey(name));
        int value = Response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "JSON value isn't equal to the exepcted value");
    }

    public static void assertJsonByName(Response Response, String name, String expectedValue){
        Response.then().assertThat().body("$",hasKey(name));
        String value = Response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "JSON value isn't equal to the exepcted value");
    }

    public static void assertJsonByNameNotEqual(Response Response, String name, String expectedValue){
        Response.then().assertThat().body("$",hasKey(name));
        String value = Response.jsonPath().getString(name);
        assertNotEquals(expectedValue, value, "JSON value is equal to the exepcted value");
    }

    public static void assertResponseTextEquals(Response Response, String expectedAnswer){
        assertEquals(
                expectedAnswer,
                Response.asString(),
                "Response text is not as expected"
        );
    }

    public static void assertResponseTextContains(Response Response, String expectedAnswer){
        assertTrue(
                Response.asString().contains(expectedAnswer),
                "Response doesn't contain " + expectedAnswer
        );
    }

    public static void assertResponseCodeEquals(Response Response, int expectedStatusCode){
        assertEquals(
                expectedStatusCode,
                Response.statusCode(),
                "Response status code is not as expected"
        );
    }

    public static void assertJsonHasField(Response Response, String expectedFieldName){
        Response.then().assertThat().body("$",hasKey(expectedFieldName));
    }

    public static void assertJsonHasFields(Response Response, String[] expectedFieldsName){
        for (String expectedFieldName : expectedFieldsName){
            Assertions.assertJsonHasField(Response, expectedFieldName);
        }
    }

    public static void assertJsonHasNotField(Response Response, String unexceptedFieldName){
        Response.then().assertThat().body("$",not(hasKey(unexceptedFieldName)));
    }


}
