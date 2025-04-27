package com.prasad_v.tests.integration;

/*
    Create Booking → Delete it → Verify it's deleted
    Steps:
        ✔ Create a booking and get bookingid.
        ✔ Delete that booking using the DELETE request.
        ✔ Verify that the booking is no longer accessible (GET /booking/{id} should return 404).
*/

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

public class E2ETest_Assignment1 {
    private static final String BASE_URL = "https://restful-booker.herokuapp.com";

    public int createBooking() {
        String requestBody = "{ \"firstname\": \"John\", \"lastname\": \"Doe\", \"totalprice\": 150, \"depositpaid\": true, \"bookingdates\": { \"checkin\": \"2025-03-25\", \"checkout\": \"2025-03-30\" }, \"additionalneeds\": \"Breakfast\" }";

        Response response = given()
                .baseUri(BASE_URL)
                .basePath("/booking")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post();

        response.then().log().all();
        Assert.assertEquals(response.statusCode(), 200);

        return response.jsonPath().getInt("bookingid");
    }

    public void deleteBooking(int bookingId, String token) {
        given()
                .baseUri(BASE_URL)
                .basePath("/booking/" + bookingId)
                .header("Cookie", "token=" + token)
                .when()
                .delete()
                .then()
                .log().all()
                .statusCode(201);
    }

    @Test
    public void testCreateDeleteVerifyBooking() {
        int bookingId = createBooking();
        String token = "2c4a5606ad3dc3b";  // Replace with actual token

        deleteBooking(bookingId, token);

        // Verify the booking is deleted (should return 404)
        given()
                .baseUri(BASE_URL)
                .basePath("/booking/" + bookingId)
                .when()
                .get()
                .then()
                .log().all()
                .statusCode(404);
    }
}
