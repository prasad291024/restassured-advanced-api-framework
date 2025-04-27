package com.prasad_v.auth;

import com.prasad_v.logging.CustomLogger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages authentication tokens for the API automation framework.
 * This class provides centralized storage, retrieval, and expiration handling for tokens.
 */
public class TokenManager {

    private static final CustomLogger logger = new CustomLogger(TokenManager.class);

    /**
     * Represents a token with its value and expiration time
     */
    private static class Token {
        private final String value;
        private final long expiresAt; // Expiration time in milliseconds since epoch

        public Token(String value, long expiresInSeconds) {
            this.value = value;
            // -1 means no expiration
            this.expiresAt = expiresInSeconds < 0 ? -1 : System.currentTimeMillis() + (expiresInSeconds * 1000);
        }

        public boolean isExpired() {
            return expiresAt != -1 && System.currentTimeMillis() > expiresAt;
        }

        public long getExpiresAt() {
            return expiresAt;
        }
    }

    // Thread-safe map to store tokens
    private static final Map<String, Token> tokenStore = new ConcurrentHashMap<>();

    // Default token keys
    public static final String DEFAULT_ACCESS_TOKEN = "access_token";
    public static final String DEFAULT_REFRESH_TOKEN = "refresh_token";

    /**
     * Stores a token with specified expiry time
     *
     * @param tokenKey Unique identifier for the token
     * @param tokenValue The token value to store
     * @param expiresInSeconds Token expiration in seconds, -1 for no expiration
     */
    public static void storeToken(String tokenKey, String tokenValue, long expiresInSeconds) {
        logger.debug("Storing token: " + tokenKey + " with expiry: " +
                (expiresInSeconds < 0 ? "never" : expiresInSeconds + " seconds"));
        Token token = new Token(tokenValue, expiresInSeconds);
        tokenStore.put(tokenKey, token);
    }

    /**
     * Gets a token from the store, if it exists and isn't expired
     *
     * @param tokenKey Unique identifier for the token
     * @return The token value, or null if not found or expired
     */
    public static String getToken(String tokenKey) {
        Token token = tokenStore.get(tokenKey);

        if (token == null) {
            logger.debug("Token not found in store: " + tokenKey);
            return null;
        }

        if (token.isExpired()) {
            logger.debug("Token found but expired: " + tokenKey);
            tokenStore.remove(tokenKey);
            return null;
        }

        logger.debug("Retrieved valid token: " + tokenKey);
        return token.value;
    }

    /**
     * Checks if a token exists and is not expired
     *
     * @param tokenKey Unique identifier for the token
     * @return true if token exists and is valid, false otherwise
     */
    public static boolean hasValidToken(String tokenKey) {
        Token token = tokenStore.get(tokenKey);
        return token != null && !token.isExpired();
    }

    /**
     * Checks if a token is expired
     *
     * @param tokenKey Unique identifier for the token
     * @return true if token is expired or doesn't exist, false if token is valid
     */
    public static boolean isTokenExpired(String tokenKey) {
        Token token = tokenStore.get(tokenKey);
        return token == null || token.isExpired();
    }

    /**
     * Gets the expiry time of a token
     *
     * @param tokenKey Unique identifier for the token
     * @return Expiry time in milliseconds since epoch, or -1 if token never expires or doesn't exist
     */
    public static long getTokenExpiry(String tokenKey) {
        Token token = tokenStore.get(tokenKey);
        return token != null ? token.getExpiresAt() : -1;
    }

    /**
     * Removes a token from the store
     *
     * @param tokenKey Unique identifier for the token
     */
    public static void removeToken(String tokenKey) {
        logger.debug("Removing token: " + tokenKey);
        tokenStore.remove(tokenKey);
    }

    /**
     * Clears all tokens from the store
     */
    public static void clearAllTokens() {
        logger.debug("Clearing all tokens from token store");
        tokenStore.clear();
    }

    /**
     * Gets the remaining lifetime of a token in seconds
     *
     * @param tokenKey Unique identifier for the token
     * @return Remaining lifetime in seconds, -1 if token never expires, 0 if expired or doesn't exist
     */
    public static long getTokenRemainingLifetime(String tokenKey) {
        Token token = tokenStore.get(tokenKey);

        if (token == null) {
            return 0;
        }

        if (token.expiresAt == -1) {
            return -1; // Never expires
        }

        long remainingMs = token.expiresAt - System.currentTimeMillis();
        return remainingMs > 0 ? remainingMs / 1000 : 0;
    }

    /**
     * Updates the expiration time of an existing token
     *
     * @param tokenKey Unique identifier for the token
     * @param newExpiresInSeconds New expiration time in seconds
     * @return true if token was updated, false if token doesn't exist
     */
    public static boolean updateTokenExpiry(String tokenKey, long newExpiresInSeconds) {
        Token token = tokenStore.get(tokenKey);

        if (token == null) {
            logger.debug("Cannot update expiry for non-existent token: " + tokenKey);
            return false;
        }

        logger.debug("Updating token expiry: " + tokenKey + " to " +
                (newExpiresInSeconds < 0 ? "never expire" : newExpiresInSeconds + " seconds"));
        Token newToken = new Token(token.value, newExpiresInSeconds);
        tokenStore.put(tokenKey, newToken);
        return true;
    }

    /**
     * Gets the default access token if available
     *
     * @return The default access token, or null if not found or expired
     */
    public static String getDefaultAccessToken() {
        return getToken(DEFAULT_ACCESS_TOKEN);
    }

    /**
     * Gets the default refresh token if available
     *
     * @return The default refresh token, or null if not found or expired
     */
    public static String getDefaultRefreshToken() {
        return getToken(DEFAULT_REFRESH_TOKEN);
    }
}