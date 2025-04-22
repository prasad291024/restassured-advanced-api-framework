package com.prasad_v.modules;

// Importing Gson library for JSON conversion (Java <-> JSON)
import com.google.gson.Gson;

// Importing POJO classes from the `pojos` package
import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.Bookingdates;
import com.prasad_v.pojos.BookingResponse;
import com.prasad_v.pojos.Auth;
import com.prasad_v.pojos.TokenResponse;

// This Java class, PayloadManager, is responsible for creating and managing JSON payloads for API requests.
// It provides methods to convert Java objects to JSON strings and vice versa.
// The class uses the Gson library for JSON serialization and deserialization.
// The methods in this class are used to create payloads for different API endpoints, such as creating a booking, updating a booking, and authenticating a user.
// The class also includes methods to convert JSON responses to Java objects for further processing.
// The purpose of this class is to centralize the creation and management of JSON payloads, making it easier to maintain and update the payloads across the project.

public class PayloadManager {

    // Gson object to handle serialization (Java -> JSON) and deserialization (JSON -> Java)
    Gson gson;

    /**
     * This method creates a booking payload by instantiating a Booking object,
     * setting its fields, and then converting it to a JSON string.
     * Used as the request body for POST /booking
     *
     * @return JSON string representing a booking
     */
    public String createPayloadBookingAsString() {
        // Creating Booking object and setting basic fields
        Booking booking = new Booking();
        booking.setFirstname("Prasad");
        booking.setLastname("Valiv");
        booking.setTotalprice(143);
        booking.setDepositpaid(true);

        // Creating Bookingdates object and setting check-in/check-out
        Bookingdates bookingdates = new Bookingdates();
        bookingdates.setCheckin("2024-02-01");
        bookingdates.setCheckout("2024-02-01");

        // Adding booking dates to the booking object
        booking.setBookingdates(bookingdates);

        // Setting additional needs like meals, etc.
        booking.setAdditionalneeds("Dinner");

        // Printing the Java object for verification
        System.out.println(booking);

        // Converting Java object to JSON string
        Gson gson = new Gson();
        String jsonStringBooking = gson.toJson(booking);

        // Printing the JSON payload to console
        System.out.println(jsonStringBooking);

        // Returning the JSON string
        return jsonStringBooking;
    }

    /**
     * Converts JSON response string to a BookingResponse Java object
     * Used to parse API responses (e.g. after creating a booking)
     *
     * @param responseString The JSON response body
     * @return Deserialized BookingResponse object
     */
    public BookingResponse bookingResponseJava(String responseString) {
        gson = new Gson(); // Initialize Gson
        BookingResponse bookingResponse = gson.fromJson(responseString, BookingResponse.class);
        return bookingResponse;
    }

    /**
     * Creates an authentication payload for /auth API.
     * Username: admin, Password: password123
     *
     * @return JSON string to be used as request body
     */
    public String setAuthPayload() {
        // Creating an Auth POJO and setting credentials
        Auth auth = new Auth();
        auth.setUsername("admin");
        auth.setPassword("password123");

        // Convert to JSON
        gson = new Gson();
        String jsonPayloadString = gson.toJson(auth);

        // Print the payload for logging/debug
        System.out.println("Payload set to the -> " + jsonPayloadString);

        // Return JSON string
        return jsonPayloadString;
    }

    /**
     * Extracts the token string from the JSON login response
     *
     * @param tokenResponse The JSON string response from /auth
     * @return Extracted token value as String
     */
    public String getTokenFromJSON(String tokenResponse) {
        gson = new Gson();  // Initialize Gson
        TokenResponse tokenResponse1 = gson.fromJson(tokenResponse, TokenResponse.class);
        return tokenResponse1.getToken(); // Return the token from the response
    }

    /**
     * Converts a booking API response into a Booking Java object
     *
     * @param getResponse The JSON response string from GET /booking/{id}
     * @return Deserialized Booking object
     */
    public Booking getResponseFromJSON(String getResponse) {
        gson = new Gson();
        Booking booking = gson.fromJson(getResponse, Booking.class);
        return booking;
    }

    /**
     * Builds and returns the payload for a full booking update (PUT /booking/{id})
     *
     * @return JSON string with updated booking data
     */
    public String fullUpdatePayloadAsString() {
        Booking booking = new Booking();
        booking.setFirstname("Lucky");
        booking.setLastname("Charming");
        booking.setTotalprice(156);
        booking.setDepositpaid(true);

        Bookingdates bookingdates = new Bookingdates();
        bookingdates.setCheckin("2024-02-01");
        bookingdates.setCheckout("2024-02-05");
        booking.setBookingdates(bookingdates);

        booking.setAdditionalneeds("Breakfast");

        return gson.toJson(booking);
    }
}




/*
This Java class, PayloadManager, is responsible for creating and managing JSON payloads for API requests.
It provides methods to convert Java objects to JSON strings and vice versa.
The class uses the Gson library for JSON serialization and deserialization.
The methods in this class are used to create payloads for different API endpoints, such as creating a booking, updating a booking, and authenticating a user.
The class also includes methods to convert JSON responses to Java objects for further processing.
The purpose of this class is to centralize the creation and management of JSON payloads, making it easier to maintain and update the payloads across the project.

Intention of the Program
This class is a utility/helper class used to:
Build request payloads in the form of JSON (for POST, PUT requests)
Deserialize API responses from JSON into Java POJOs
Centralize and manage all the payload logic and conversion in one place
Use Gson to handle serialization (Java → JSON) and deserialization (JSON → Java)

 */


