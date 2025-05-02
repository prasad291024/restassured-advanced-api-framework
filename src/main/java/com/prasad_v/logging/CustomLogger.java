package com.prasad_v.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom logger class to provide standardized logging functionality
 * throughout the framework.
 */
public class CustomLogger {
    private Logger logger;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * Constructor that initializes the logger for a specific class
     *
     * @param clazz The class for which the logger is being created
     */
    public CustomLogger(Class<?> clazz) {
        this.logger = LogManager.getLogger(clazz);
    }

    /**
     * Log an info message
     *
     * @param message The message to log
     */
    public void info(String message) {
        logger.info(formatMessage(message));
    }

    /**
     * Log a debug message
     *
     * @param message The message to log
     */
    public void debug(String message) {
        logger.debug(formatMessage(message));
    }

    /**
     * Log a warning message
     *
     * @param message The message to log
     */
    public void warn(String message) {
        logger.warn(formatMessage(message));
    }

    /**
     * Log an error message
     *
     * @param message The message to log
     */
    public void error(String message) {
        logger.error(formatMessage(message));
    }

    /**
     * Log an error message with an exception
     *
     * @param message The message to log
     * @param throwable The exception to log
     */
    public void error(String message, Throwable throwable) {
        logger.error(formatMessage(message), throwable);
    }

    /**
     * Log API request details
     *
     * @param endpoint The API endpoint
     * @param method The HTTP method
     * @param headers The request headers
     * @param body The request body
     */
    public void logRequest(String endpoint, String method, String headers, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n====== REQUEST ======\n");
        sb.append("Endpoint: ").append(endpoint).append("\n");
        sb.append("Method: ").append(method).append("\n");
        sb.append("Headers: ").append(headers).append("\n");
        if (body != null && !body.isEmpty()) {
            sb.append("Body: ").append(body).append("\n");
        }
        sb.append("=====================\n");

        logger.info(sb.toString());
    }

    /**
     * Log API response details
     *
     * @param statusCode The response status code
     * @param responseTime The response time in milliseconds
     * @param headers The response headers
     * @param body The response body
     */
    public void logResponse(int statusCode, long responseTime, String headers, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n====== RESPONSE ======\n");
        sb.append("Status Code: ").append(statusCode).append("\n");
        sb.append("Response Time: ").append(responseTime).append(" ms\n");
        sb.append("Headers: ").append(headers).append("\n");
        if (body != null && !body.isEmpty()) {
            sb.append("Body: ").append(body).append("\n");
        }
        sb.append("======================\n");

        logger.info(sb.toString());
    }

    /**
     * Format log message with timestamp and thread information
     *
     * @param message The message to format
     * @return The formatted message
     */
    private String formatMessage(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String timestamp = sdf.format(new Date());
        String threadName = Thread.currentThread().getName();

        return String.format("[%s] [%s] %s", timestamp, threadName, message);
    }
}