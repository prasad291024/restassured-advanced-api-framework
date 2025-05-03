package com.prasad_v.requestbuilder;

import java.util.HashMap;
import java.util.Map;

import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.constants.APIConstants;
import com.prasad_v.logging.CustomLogger;

/**
 * HeaderManager handles HTTP headers for API requests.
 * It provides methods to manage common headers and custom headers.
 */
public class HeaderManager {

    private static final CustomLogger logger = new CustomLogger(HeaderManager.class);
    private Map<String, String> headers;
    private ConfigurationManager configManager;

    /**
     * Constructor initializes the headers map
     */
    public HeaderManager() {
        headers = new HashMap<>();
        configManager = ConfigurationManager.getInstance();
    }

    /**
     * Set a header with key and value
     *
     * @param key Header key
     * @param value Header value
     * @return Current HeaderManager instance for method chaining
     */
    public HeaderManager addHeader(String key, String value) {
        headers.put(key, value);
        logger.debug("Added header: " + key + " = " + value);
        return this;
    }

    /**
     * Add multiple headers from a map
     *
     * @param headersMap Map containing headers
     * @return Current HeaderManager instance for method chaining
     */
    public HeaderManager addHeaders(Map<String, String> headersMap) {
        if (headersMap != null) {
            headers.putAll(headersMap);
            logger.debug("Added multiple headers: " + headersMap.keySet());
        }
        return this;
    }

    /**
     * Remove a header
     *
     * @param key Header key to remove
     * @return Current HeaderManager instance for method chaining
     */
    public HeaderManager removeHeader(String key) {
        headers.remove(key);
        logger.debug("Removed header: " + key);
        return this;
    }

    /**
     * Clear all headers
     *
     * @return Current HeaderManager instance for method chaining
     */
    public HeaderManager clearHeaders() {
        headers.clear();
        logger.debug("Cleared all headers");
        return this;
    }

    /**
     * Add common content type header for JSON
     *
     * @return Current HeaderManager instance for method chaining
     */
    public HeaderManager addContentTypeJson() {
        headers.put(APIConstants.HEADER_CONTENT_TYPE, APIConstants.CONTENT_TYPE_JSON);
        logger.debug("Added Content-Type: application/json header");
        return this;
    }

    /**
     * Add common content type header for XML
     *
     * @return Current HeaderManager instance for method chaining
     */
    public HeaderManager addContentTypeXml() {
        headers.put(APIConstants.HEADER_CONTENT_TYPE, APIConstants.CONTENT_TYPE_XML);
        logger.debug("Added Content-Type: application/xml header");
        return this;
    }

    /**
     * Add common content type header for form data
     *
     * @return Current HeaderManager instance for method chaining
     */
    public HeaderManager addContentTypeFormData() {
        headers.put(APIConstants.HEADER_CONTENT_TYPE, APIConstants.CONTENT_TYPE_FORM);
        logger.debug("Added Content-Type: application/x-www-form-urlencoded header");
        return this;
    }

    /**
     * Add Accept header for JSON
     *
     * @return Current HeaderManager instance for method chaining
     */
    public HeaderManager addAcceptJson() {
        headers.put(APIConstants.HEADER_ACCEPT, APIConstants.CONTENT_TYPE_JSON);
        logger.debug("Added Accept: application/json header");
        return this;
    }

    /**
     * Add Accept header for XML
     *
     * @return Current HeaderManager instance for method chaining
     */
    public HeaderManager addAcceptXml() {
        headers.put(APIConstants.HEADER_ACCEPT, APIConstants.CONTENT_TYPE_XML);
        logger.debug("Added Accept: application/xml header");
        return this;
    }

    /**
     * Add common headers from configuration
     *
     * @return Current HeaderManager instance for method chaining
     */
    public HeaderManager addCommonHeaders() {
        String commonHeadersConfig = configManager.getConfigProperty("api.common.headers", "");
        if (!commonHeadersConfig.isEmpty()) {
            String[] headerPairs = commonHeadersConfig.split(";");
            for (String pair : headerPairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    headers.put(keyValue[0].trim(), keyValue[1].trim());
                    logger.debug("Added common header from config: " + keyValue[0].trim());
                }
            }
        }
        return this;
    }

    /**
     * Add authorization header
     *
     * @param token Authorization token
     * @return Current HeaderManager instance for method chaining
     */
    public HeaderManager addAuthorizationHeader(String token) {
        if (token != null && !token.isEmpty()) {
            if (!token.startsWith("Bearer ") && !token.startsWith("Basic ")) {
                token = "Bearer " + token;
            }
            headers.put(APIConstants.HEADER_AUTHORIZATION, token);
            logger.debug("Added Authorization header");
        } else {
            logger.warn("Attempted to add empty authorization token");
        }
        return this;
    }

    /**
     * Get all headers
     *
     * @return Map of all headers
     */
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }
}