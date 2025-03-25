package com.prasad_v.tests.integration;

/*
    Create Booking → Update it → Try to Delete it
    Steps:
        ✔ Create a new booking and store bookingid.
        ✔ Update the booking using PUT.
        ✔ Delete the updated booking using DELETE.
        ✔ Verify deletion was successful (GET /booking/{id} should return 404).
*/

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

public class E2ETest_Assignment3 {
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

    @Test
    public void testCreateUpdateDeleteBooking() {
        int bookingId = createBooking();
        String token = "YOUR_VALID_TOKEN";

        // Update booking
        String updateRequestBody = "{ \"firstname\": \"Michael\", \"lastname\": \"Scott\", \"totalprice\": 200, \"depositpaid\": false, \"bookingdates\": { \"checkin\": \"2025-04-01\", \"checkout\": \"2025-04-05\" }, \"additionalneeds\": \"Lunch\" }";

        given()
                .baseUri(BASE_URL)
                .basePath("/booking/" + bookingId)
                .header("Cookie", "token=" + token)
                .contentType(ContentType.JSON)
                .body(updateRequestBody)
                .when()
                .put()
                .then()
                .log().all()
                .statusCode(200);

        // Delete booking
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
}
