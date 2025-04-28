package com.prasad_v.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration manager to handle properties files for different environments
 * This class provides functionality to load and retrieve configuration properties
 */
public class ConfigurationManager {
    private static ConfigurationManager instance;
    private Properties properties;

    private ConfigurationManager() {
        properties = new Properties();
    }

    /**
     * Get the singleton instance of ConfigurationManager
     *
     * @return ConfigurationManager instance
     */
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * Load properties from a specified file path
     *
     * @param filePath Path to the properties file
     * @throws IOException If file cannot be read or doesn't exist
     */
    public void loadConfig(String filePath) throws IOException {
        try (InputStream input = new FileInputStream(filePath)) {
            properties = new Properties();
            properties.load(input);
        }
    }

    /**
     * Load properties from classpath resource
     *
     * @param resourcePath Path to the resource file in classpath
     * @throws IOException If resource cannot be read or doesn't exist
     */
    public void loadConfigFromResource(String resourcePath) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            properties = new Properties();
            properties.load(input);
        }
    }

    /**
     * Get a property value as string
     *
     * @param key Property key
     * @return Property value or null if not found
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get a property value as string with default value if not found
     *
     * @param key Property key
     * @param defaultValue Default value to return if property is not found
     * @return Property value or default value if not found
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get a property value as integer
     *
     * @param key Property key
     * @param defaultValue Default value to return if property is not found or not an integer
     * @return Property value as integer or default value
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Get a property value as boolean
     *
     * @param key Property key
     * @param defaultValue Default value to return if property is not found
     * @return Property value as boolean or default value
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * Set a property value
     *
     * @param key Property key
     * @param value Property value
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * Check if a property exists
     *
     * @param key Property key
     * @return true if property exists, false otherwise
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * Clear all loaded properties
     */
    public void clearProperties() {
        properties.clear();
    }
}