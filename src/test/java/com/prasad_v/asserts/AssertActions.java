package com.prasad_v.asserts;

// Import RestAssured's Response class to access HTTP response details
import io.restassured.response.Response;

// Import TestNG's assertEquals for standard assertions
import static org.testng.Assert.assertEquals;

// Import AssertJ for fluent and readable assertions
import static org.assertj.core.api.Assertions.*;

public class AssertActions {

    /**
     * Validates that two String values are equal.
     * Useful for comparing expected vs actual values from the response body.
     *
     * @param actual      The actual value from response
     * @param expected    The expected value to compare
     * @param description Custom message to display if assertion fails
     */
    public void verifyResponseBody(String actual, String expected, String description) {
        assertEquals(actual, expected, description);
    }

    /**
     * Validates that two int values are equal.
     * Often used for numerical comparisons (like ID, total price, status codes, etc.)
     *
     * @param actual      The actual int value
     * @param expected    The expected int value
     * @param description Custom message if assertion fails
     */
    public void verifyResponseBody(int actual, int expected, String description) {
        assertEquals(actual, expected, description);
    }

    /**
     * Validates the status code of an API response.
     *
     * @param response The RestAssured response object
     * @param expected The expected status code (e.g., 200, 201, 404)
     */
    public void verifyStatusCode(Response response, Integer expected) {
        assertEquals(response.getStatusCode(), expected);
    }

    /**
     * Validates that a string value:
     * - Is not null
     * - Is not blank
     * - Matches the expected value
     *
     * @param keyExpect The expected value
     * @param keyActual The actual value from response
     */
    public void verifyStringKey(String keyExpect, String keyActual) {
        // Check that expected string is not null
        assertThat(keyExpect).isNotNull();

        // Also check that it's not blank or empty
        assertThat(keyExpect).isNotBlank();

        // Check equality between expected and actual
        assertThat(keyExpect).isEqualTo(keyActual);
    }

    /**
     * Validates that an Integer key (e.g., booking ID) is not null.
     *
     * @param keyExpect The integer to check
     */
    public void verifyStringKeyNotNull(Integer keyExpect) {
        assertThat(keyExpect).isNotNull();
    }

    /**
     * Overloaded method to validate that a String key is not null.
     *
     * @param keyExpect The string to check
     */
    public void verifyStringKeyNotNull(String keyExpect) {
        assertThat(keyExpect).isNotNull();
    }
}













/*
This program is a part of the com.prasad_v.asserts package and is used for performing various assertions.

It includes methods for validating the status code, response body (strings and integers), and checking for null values.
The methods use the following libraries:

- RestAssured for accessing HTTP response details
- TestNG for standard assertions
- AssertJ for fluent and readable assertions
- Java's assertEquals for standard assertions
- Java's assertThat for fluent assertions
- Java's Integer class for integer operations
- Java's String class for string operations
- Java's Boolean class for boolean operations
- Java's Object class for general object operations
- Java's Class class for class operations
- Java's Throwable class for exception handling
- Java's Exception class for exception handling
- Java's RuntimeException class for runtime exceptions
- Java's NullPointerException class for handling null pointer exceptions

 */