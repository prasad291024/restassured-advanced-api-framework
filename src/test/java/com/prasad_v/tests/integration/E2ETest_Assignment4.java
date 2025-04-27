package com.prasad_v.tests.integration;

/*
    Delete a Booking → Try to Update it
    Steps:
        ✔ Create a new booking and get bookingid.
        ✔ Delete that booking.
        ✔ Try to update the deleted booking.
        ✔ Validate update fails with 405 (Method Not Allowed) or 404 (Not Found).
*/

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

public class E2ETest_Assignment4 {
    private static final String BASE_URL = "https://restful-booker.herokuapp.com";

    @Test
    public void testDeleteThenTryToUpdateBooking() {
        int bookingId = 100;  // Provide an existing booking ID
        String token = "2c4a5606ad3dc3b";

        // Delete the booking
        given()
                .baseUri(BASE_URL)
                .basePath("/booking/" + bookingId)
                .header("Cookie", "token=" + token)
                .when()
                .delete()
                .then()
                .log().all()
                .statusCode(201);

        // Try to update the deleted booking
        String updateRequestBody = "{ \"firstname\": \"James\", \"lastname\": \"Bond\" }";

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
                .statusCode(405);
    }
}
