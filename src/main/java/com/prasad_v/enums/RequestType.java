package com.prasad_v.enums;

/**
 * Enum representing different HTTP request types used in API testing.
 * This enum provides constants for HTTP methods and utility methods for
 * working with these request types.
 */
public enum RequestType {

    /**
     * HTTP GET method. Used to retrieve data from a specified resource.
     * GET requests should only retrieve data and not modify it.
     */
    GET,

    /**
     * HTTP POST method. Used to submit data to be processed to a specified resource.
     * Often used to create new resources or trigger operations.
     */
    POST,

    /**
     * HTTP PUT method. Used to update a resource or create it if it doesn't exist.
     * PUT requests are idempotent, meaning repeated identical requests will have the same effect.
     */
    PUT,

    /**
     * HTTP DELETE method. Used to delete a specified resource.
     */
    DELETE,

    /**
     * HTTP PATCH method. Used to apply partial modifications to a resource.
     * Unlike PUT, PATCH is not necessarily idempotent.
     */
    PATCH,

    /**
     * HTTP HEAD method. Similar to GET but without the response body.
     * Used to obtain metadata about the resource without transferring the entire content.
     */
    HEAD,

    /**
     * HTTP OPTIONS method. Used to describe the communication options for the target resource.
     * Allows a client to determine the available options or requirements of a resource.
     */
    OPTIONS;

    /**
     * Checks if the request type allows a request body
     *
     * @return true if the request type can have a body, false otherwise
     */
    public boolean allowsRequestBody() {
        return this == POST || this == PUT || this == PATCH;
    }

    /**
     * Checks if the request type is idempotent (same request can be repeated without different outcomes)
     *
     * @return true if the request type is idempotent, false otherwise
     */
    public boolean isIdempotent() {
        return this != POST && this != PATCH;
    }

    /**
     * Checks if the request type is safe (does not modify resources)
     *
     * @return true if the request type is safe, false otherwise
     */
    public boolean isSafe() {
        return this == GET || this == HEAD || this == OPTIONS;
    }

    /**
     * Checks if the request type can modify server state
     *
     * @return true if the request type can modify state, false otherwise
     */
    public boolean canModifyState() {
        return !isSafe();
    }

    /**
     * Convert a string to a RequestType enum value
     *
     * @param method The HTTP method as a string
     * @return The corresponding RequestType enum value
     * @throws IllegalArgumentException If the method string does not match any enum value
     */
    public static RequestType fromString(String method) {
        try {
            return valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method, e);
        }
    }

    /**
     * Get the request type as a string
     *
     * @return The HTTP method name as a string
     */
    public String toMethodString() {
        return this.name();
    }
}