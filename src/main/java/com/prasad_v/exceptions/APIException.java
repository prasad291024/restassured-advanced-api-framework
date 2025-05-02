package com.prasad_v.exceptions;

import io.restassured.response.Response;

/**
 * Custom exception class for API automation framework.
 * This class encapsulates exceptions that occur during API testing
 * and provides additional context specific to API operations.
 */
public class APIException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int statusCode;
    private String requestUrl;
    private String requestMethod;
    private String responseBody;
    private Response response;
    private String errorCode;

    /**
     * Constructs a new APIException with the specified detail message.
     *
     * @param message The detail message
     */
    public APIException(String message) {
        super(message);
    }

    /**
     * Constructs a new APIException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public APIException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new APIException with the specified detail message, status code, and response body.
     *
     * @param message The detail message
     * @param statusCode The HTTP status code
     * @param responseBody The response body as a string
     */
    public APIException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Constructs a new APIException with details from a RestAssured Response object.
     *
     * @param message The detail message
     * @param response The RestAssured Response object
     */
    public APIException(String message, Response response) {
        super(message);
        this.response = response;
        if (response != null) {
            this.statusCode = response.getStatusCode();
            this.responseBody = response.getBody() != null ? response.getBody().asString() : null;
        }
    }

    /**
     * Constructs a new APIException with full request and response details.
     *
     * @param message The detail message
     * @param requestUrl The request URL
     * @param requestMethod The request method
     * @param statusCode The HTTP status code
     * @param responseBody The response body as a string
     */
    public APIException(String message, String requestUrl, String requestMethod, int statusCode, String responseBody) {
        super(message);
        this.requestUrl = requestUrl;
        this.requestMethod = requestMethod;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Gets the HTTP status code associated with this exception.
     *
     * @return The HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the request URL associated with this exception.
     *
     * @return The request URL
     */
    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * Gets the request method associated with this exception.
     *
     * @return The request method
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * Gets the response body associated with this exception.
     *
     * @return The response body as a string
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * Gets the RestAssured Response object associated with this exception.
     *
     * @return The Response object
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Gets the error code associated with this exception.
     *
     * @return The error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the error code for this exception.
     *
     * @param errorCode The error code
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Sets the request URL for this exception.
     *
     * @param requestUrl The request URL
     */
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    /**
     * Sets the request method for this exception.
     *
     * @param requestMethod The request method
     */
    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * Sets the HTTP status code for this exception.
     *
     * @param statusCode The HTTP status code
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Sets the response body for this exception.
     *
     * @param responseBody The response body as a string
     */
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * Sets the RestAssured Response object for this exception.
     *
     * @param response The Response object
     */
    public void setResponse(Response response) {
        this.response = response;
        if (response != null) {
            this.statusCode = response.getStatusCode();
            this.responseBody = response.getBody() != null ? response.getBody().asString() : null;
        }
    }

    /**
     * Returns a string representation of this exception including API-specific details.
     *
     * @return A string representation of this exception
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(": ").append(getMessage()).append("\n");

        if (requestMethod != null && requestUrl != null) {
            sb.append("Request: ").append(requestMethod).append(" ").append(requestUrl).append("\n");
        }

        if (statusCode > 0) {
            sb.append("Status Code: ").append(statusCode).append("\n");
        }

        if (errorCode != null) {
            sb.append("Error Code: ").append(errorCode).append("\n");
        }

        if (responseBody != null && !responseBody.isEmpty()) {
            // Limit response body in toString to avoid overwhelming logs
            String truncatedBody = responseBody.length() > 500 ?
                    responseBody.substring(0, 500) + "... (truncated)" : responseBody;
            sb.append("Response Body: ").append(truncatedBody).append("\n");
        }

        if (getCause() != null) {
            sb.append("Caused by: ").append(getCause());
        }

        return sb.toString();
    }
}