package com.prasad_v.tests.integration;

/**
 * This class automates an end-to-end test flow for:
 * 1. Creating a booking.
 * 2. Verifying that the booking exists.
 * 3. Deleting the booking.
 * 4. Verifying that the booking has been successfully deleted.
 */

import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.endpoints.APIConstants;
import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.BookingResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestE2EFlow_02 extends BaseTest {

    /**
     * Step 1: Create a Booking
     * - Sends POST request
     * - Extracts and stores booking ID
     */
    @Test(priority = 1)
    @Owner("Prasad")
    @Description("TC#E2E2 - Step 1: Create a booking and store booking ID")
    public void createBooking(ITestContext context) {
        // Setting the API endpoint path for creating a booking
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);

        // Sending a POST request with the booking payload
        response = RestAssured.given(requestSpecification)
                .body(payloadManager.createPayloadBookingAsString()) // Generates JSON payload using PayloadManager
                .post();

        // Logging response and validating HTTP status code
        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200); // Expecting 200 OK on successful creation

        // Deserialize the response JSON into a BookingResponse Java object
        BookingResponse bookingResponse = payloadManager.bookingResponseJava(response.asString());

        // Validate first name is 'Pramod' and booking ID is not null
        assertActions.verifyStringKey(bookingResponse.getBooking().getFirstname(), "Pramod");
        assertActions.verifyStringKeyNotNull(bookingResponse.getBookingid());

        // Store booking ID in TestNG context for use in other test methods
        context.setAttribute("bookingid", bookingResponse.getBookingid());
    }


    /**
     * Step 2: Verify Booking was Created
     * - Sends GET request to check booking exists
     */
    @Test(priority = 2)
    @Owner("Prasad")
    @Description("TC#E2E2 - Step 2: Verify booking by ID exists after creation")
    public void verifyBooking(ITestContext context) {

        // Retrieve booking ID from TestNG context
        Integer bookingId = (Integer) context.getAttribute("bookingid");

        // Construct the GET path to fetch specific booking
        String getPath = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId;
        requestSpecification.basePath(getPath);

        // Send GET request to fetch the booking
        response = RestAssured.given(requestSpecification).get();

        // Log and validate response
        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200); // Expecting 200 OK

        // Convert JSON response into Booking Java object
        Booking booking = payloadManager.getResponseFromJSON(response.asString());

        // Validate that first name is present and not blank
        assertThat(booking.getFirstname()).isNotBlank();
    }


    /**
     * Step 3: Delete the Booking
     * - Requires authentication token
     */
    @Test(priority = 3)
    @Owner("Prasad")
    @Description("TC#E2E2 - Step 3: Delete the booking using token")
    public void deleteBooking(ITestContext context) {
        // Get booking ID from context
        Integer bookingId = (Integer) context.getAttribute("bookingid");

        // Generate token and store it for later use
        String token = getToken();
        context.setAttribute("token", token);

        // Build the DELETE path for the specific booking ID
        String deletePath = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId;

        // Set base path and attach token in request cookie
        requestSpecification.basePath(deletePath).cookie("token", token);

        // Send DELETE request to remove the booking
        validatableResponse = RestAssured.given()
                .spec(requestSpecification)
                .delete()
                .then()
                .log().all();

        // Verify that deletion was successful
        validatableResponse.statusCode(201); // Expecting 201 Created → deletion success in this API
    }

    /**
     * Step 4: Verify Booking is Deleted
     * - Expects 404 when accessing deleted booking
     */
    @Test(priority = 4)
    @Owner("Prasad")
    @Description("TC#E2E2 - Step 4: Verify booking no longer exists")
    public void verifyBookingDeleted(ITestContext context) {
        // Retrieve booking ID from context
        Integer bookingId = (Integer) context.getAttribute("bookingid");

        // Build GET request path for deleted booking
        String getPath = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId;
        requestSpecification.basePath(getPath);

        // Send GET request
        validatableResponse = RestAssured.given()
                .spec(requestSpecification)
                .get()
                .then()
                .log().all();

        // Verify booking is no longer found (404 Not Found)
        validatableResponse.statusCode(404);
    }
}
