package com.prasad_v.endpoints; // Package declaration - Organizes the class under a specific namespace.

public class APIConstants { // Class declaration for storing API-related constants.

    // Base URL of the RESTful web service
    public static String BASE_URL = "https://restful-booker.herokuapp.com";
    // public static String BASE_URL = // Fetch this from Excel file


    // In the future, instead of hardcoding, we may fetch the BASE_URL from an external Excel file.
    // Example:
    // public static String BASE_URL = ExcelReader.getCellValue("Config.xlsx", "Sheet1", "BaseURL");

    // API Endpoint for Creating, Updating, and Deleting Bookings
    // Used in API calls where we manage user bookings.
    public static String CREATE_UPDATE_BOOKING_URL = "/booking";

    // Authentication Endpoint
    // Used to generate authentication tokens for API requests.
    public static String AUTH_URL = "/auth";

    // Health Check Endpoint
    // This endpoint is used to check if the API server is online and functioning correctly.
    public static String PING_URL = "/ping";
}

/*
This Java class, APIConstants, defines a set of constants (static string variables) that represent API endpoints for a RESTful web service.
The intention of this program is to centralize and store API endpoint URLs in one place so that they can be easily accessed and maintained across the project.

This class stores API endpoint constants for easy access across the project.
The BASE_URL represents the main API URL.
The other constants define specific API paths used for:
Creating/Updating Bookings (/booking).
User Authentication (/auth).
Checking Server Health (/ping).
These constants help maintain clean and reusable code instead of hardcoding URLs in multiple places.
They also make it easier to update the API endpoints if needed, as they are defined in one central location.
 */