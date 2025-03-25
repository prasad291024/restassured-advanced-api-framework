package com.prasad_v.tests.integration;

/*
    Get a Booking from Get All → Try to Delete it
    Steps:
        ✔ Fetch all bookings (GET /booking) and get one bookingid.
        ✔ Try deleting that booking without authentication.
        ✔ Validate that deletion fails with 403 (Forbidden).
*/

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

public class E2ETest_Assignment2 {
    private static final String BASE_URL = "https://restful-booker.herokuapp.com";

    @Test
    public void testGetAndTryToDeleteBooking() {
        // Fetch all bookings
        Response response = given()
                .baseUri(BASE_URL)
                .basePath("/booking")
                .when()
                .get();

        response.then().log().all();
        Assert.assertEquals(response.statusCode(), 200);

        // Pick the first booking ID from response
        int bookingId = response.jsonPath().getInt("[0].bookingid");

        // Try to delete the booking WITHOUT authentication
        given()
                .baseUri(BASE_URL)
                .basePath("/booking/" + bookingId)
                .when()
                .delete()
                .then()
                .log().all()
                .statusCode(403);  // Expecting 403 Forbidden
    }
}
