package com.prasad_v.interceptors;

import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.logging.CustomLogger;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Interceptor for HTTP requests and responses.
 * This class provides functionality to intercept, log, and measure API requests and responses.
 */
public class RequestResponseInterceptor implements Filter {

    private static final CustomLogger logger = new CustomLogger(RequestResponseInterceptor.class);
    private static final Map<String, RequestInfo> requestInfoMap = new HashMap<>();

    /**
     * Inner class to hold request information
     */
    private static class RequestInfo {
        Instant startTime;
        String requestId;
        String method;
        String url;
        String requestBody;
    }

    /**
     * Creates a new RequestSpecification with this interceptor added
     *
     * @param requestSpecification The original request specification
     * @return A new RequestSpecification with the interceptor added
     */
    public static RequestSpecification addInterceptor(RequestSpecification requestSpecification) {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.addFilter(new RequestResponseInterceptor());

        boolean enableLogging = ConfigurationManager.getInstance().getBooleanProperty("api.logging.enabled", true);
        if (enableLogging) {
            ByteArrayOutputStream requestLog = new ByteArrayOutputStream();
            ByteArrayOutputStream responseLog = new ByteArrayOutputStream();
            PrintStream requestPrintStream = new PrintStream(requestLog);
            PrintStream responsePrintStream = new PrintStream(responseLog);

            builder.addFilter(new RequestLoggingFilter(LogDetail.ALL, requestPrintStream));
            builder.addFilter(new ResponseLoggingFilter(LogDetail.ALL, responsePrintStream));
        }

        return requestSpecification.spec(builder.build());
    }

    /**
     * The filter implementation that intercepts requests and responses
     *
     * @param requestSpec The request specification
     * @param responseSpec The response specification
     * @param filterContext The filter context
     * @return The response after processing
     */
    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext filterContext) {

        String requestId = UUID.randomUUID().toString();
        Instant startTime = Instant.now();

        // Add correlation ID header if not already present
        if (!requestSpec.hasHeader("X-Correlation-ID")) {
            requestSpec.header("X-Correlation-ID", requestId);
        }

        // Store request info
        RequestInfo info = new RequestInfo();
        info.startTime = startTime;
        info.requestId = requestId;
        info.method = requestSpec.getMethod();
        info.url = requestSpec.getURI();
        info.requestBody = requestSpec.getBody() != null ? requestSpec.getBody().toString() : null;
        requestInfoMap.put(requestId, info);

        // Log request
        logger.debug("Starting API request [" + requestId + "]: " + info.method + " " + info.url);

        // Execute request and capture response
        Response response = filterContext.next(requestSpec, responseSpec);

        // Calculate duration
        Instant endTime = Instant.now();
        long durationMs = ChronoUnit.MILLIS.between(startTime, endTime);

        // Log response
        int statusCode = response.getStatusCode();
        logger.debug("API response [" + requestId + "]: Status " + statusCode +
                " (" + durationMs + "ms): " + info.method + " " + info.url);

        // Add response time as a property to the response object
        if (response instanceof RestAssuredResponseImpl) {
            ((RestAssuredResponseImpl) response).setProperty("responseTimeInMs", durationMs);
        }

        // Log extra information for non-2xx responses
        if (statusCode < 200 || statusCode >= 300) {
            String responseBody = response.getBody() != null ? response.getBody().asString() : "No body";
            if (responseBody.length() > 1000) {
                responseBody = responseBody.substring(0, 997) + "...";
            }
            logger.warn("Non-successful API response [" + requestId + "]: " + statusCode + " - " + responseBody);
        }

        // Remove request info from map to avoid memory leaks
        requestInfoMap.remove(requestId);

        return response;
    }

    /**
     * Gets response time for a specific request
     *
     * @param response The response object
     * @return Response time in milliseconds or -1 if not available
     */
    public static long getResponseTime(Response response) {
        if (response instanceof RestAssuredResponseImpl) {
            Object responseTime = ((RestAssuredResponseImpl) response).getProperty("responseTimeInMs");
            if (responseTime instanceof Long) {
                return (Long) responseTime;
            }
        }
        return -1;
    }

    /**
     * Clears the interceptor's internal state
     * Should be called periodically to ensure no memory leaks
     */
    public static void clearState() {
        requestInfoMap.clear();
    }

    /**
     * Helper method to truncate strings that are too large
     *
     * @param input The input string
     * @param maxLength Maximum length before truncation
     * @return The truncated string
     */
    private static String truncate(String input, int maxLength) {
        if (input == null) {
            return "null";
        }
        return input.length() <= maxLength ? input : input.substring(0, maxLength) + "...";
    }
}