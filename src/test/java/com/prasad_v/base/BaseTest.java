package com.prasad_v.base;

import com.prasad_v.endpoints.APIConstants;
import com.prasad_v.asserts.AssertActions;
import com.prasad_v.modules.PayloadManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeTest;

/**
 * BaseTest class provides a foundation for all API tests.
 * It sets up common configurations such as base URL, headers, and authentication token retrieval.
 */
public class BaseTest {

    // Common variables for API testing
    public RequestSpecification requestSpecification;  // Holds common request settings
    public AssertActions assertActions;  // Manages assertions for validation
    public PayloadManager payloadManager;  // Handles API payloads
    public JsonPath jsonPath;  // Helps parse JSON responses
    public Response response;  // Stores raw API responses
    public ValidatableResponse validatableResponse;  // Stores responses in a testable format

    /**
     * Setup method runs before every test.
     * It initializes request specifications, payload management, and assertion handling.
     */
    @BeforeTest
    public void setUp() {
        // Initialize payload manager and assertion utility
        payloadManager = new PayloadManager();
        assertActions = new AssertActions();

        // Create a reusable request specification for all tests
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(APIConstants.BASE_URL)  // Set the API base URL
                .addHeader("Content-Type", "application/json")  // Set header for JSON requests
                .build().log().all();  // Log request details
    }

    /**
     * Fetches an authentication token for secured API requests.
     * @return The generated authentication token as a String.
     */
    public String getToken() {
        // Create a new request specification for authentication
        requestSpecification = RestAssured
                .given()
                .baseUri(APIConstants.BASE_URL)  // Set API base URL
                .basePath(APIConstants.AUTH_URL);  // Set authentication endpoint

        // Get the authentication payload (e.g., username & password)
        String payload = payloadManager.setAuthPayload();

        // Send POST request to obtain the authentication token
        response = requestSpecification
                .contentType(ContentType.JSON)  // Set content type as JSON
                .body(payload)  // Attach the authentication payload
                .when()
                .post();  // Perform the POST request

        // Extract the authentication token from the response
        String token = payloadManager.getTokenFromJSON(response.asString());

        // Return the token for future API requests
        return token;
    }
}










/*
This is the base class for all API tests.
It sets up common configurations such as base URL, headers, and authentication token retrieval.
The @BeforeTest annotation ensures that this setup is performed before running any tests.
The getToken() method fetches an authentication token for secured API requests.
The payloadManager and assertActions objects are initialized in the setUp() method for reuse across tests.
The requestSpecification object is created as a reusable RequestSpecBuilder instance for all tests.
The setUp() method is executed before each test class to ensure a fresh state for each test.
This class serves as a foundation for all API tests and can be extended by other test classes.
Any common functionality or setup required for all API tests can be added to this class.
 */