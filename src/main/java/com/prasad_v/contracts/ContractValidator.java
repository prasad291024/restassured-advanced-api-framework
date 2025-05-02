package com.prasad_v.contracts;

import com.prasad_v.exceptions.APIException;
import com.prasad_v.logging.CustomLogger;
import io.restassured.response.Response;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for validating API responses against contract specifications.
 * This class provides functionality to validate that API responses conform to
 * expected schemas and contracts.
 */
public class ContractValidator {

    private static final CustomLogger logger = new CustomLogger(ContractValidator.class);
    private static final Map<String, Schema> schemaCache = new HashMap<>();

    /**
     * Validates an API response against a JSON schema
     *
     * @param response The API response to validate
     * @param schemaPath Path to the JSON schema file
     * @return True if validation passes, throws exception otherwise
     * @throws APIException If validation fails or schema cannot be loaded
     */
    public static boolean validateAgainstSchema(Response response, String schemaPath) throws APIException {
        try {
            Schema schema = getSchemaFromCache(schemaPath);
            JSONObject responseJson = new JSONObject(response.getBody().asString());

            schema.validate(responseJson);
            logger.info("Schema validation successful for: " + schemaPath);
            return true;
        } catch (ValidationException e) {
            List<String> validationErrors = new ArrayList<>();
            collectValidationErrors(e, validationErrors);
            String errorMessage = "Schema validation failed. Errors: " + String.join(", ", validationErrors);
            logger.error(errorMessage);
            throw new APIException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = "Error during schema validation: " + e.getMessage();
            logger.error(errorMessage);
            throw new APIException(errorMessage, e);
        }
    }

    /**
     * Validates an API response against contract expectations for specific fields
     *
     * @param response The API response to validate
     * @param contractPath Path to the contract definition file
     * @return True if validation passes, throws exception otherwise
     * @throws APIException If validation fails or contract cannot be loaded
     */
    public static boolean validateAgainstContract(Response response, String contractPath) throws APIException {
        try {
            JSONObject contract = loadJsonFromFile(contractPath);
            JSONObject responseJson = new JSONObject(response.getBody().asString());

            List<String> validationErrors = new ArrayList<>();
            validateJsonAgainstContract(responseJson, contract, "", validationErrors);

            if (!validationErrors.isEmpty()) {
                String errorMessage = "Contract validation failed. Errors: " + String.join(", ", validationErrors);
                logger.error(errorMessage);
                throw new APIException(errorMessage);
            }

            logger.info("Contract validation successful for: " + contractPath);
            return true;
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            String errorMessage = "Error during contract validation: " + e.getMessage();
            logger.error(errorMessage);
            throw new APIException(errorMessage, e);
        }
    }

    /**
     * Validates that all required fields exist in an API response
     *
     * @param response The API response to validate
     * @param requiredFields Array of required field paths (e.g., "user.id", "user.name")
     * @return True if all required fields exist, throws exception otherwise
     * @throws APIException If any required field is missing
     */
    public static boolean validateRequiredFields(Response response, String[] requiredFields) throws APIException {
        try {
            JSONObject responseJson = new JSONObject(response.getBody().asString());
            List<String> missingFields = new ArrayList<>();

            for (String field : requiredFields) {
                if (!fieldExists(responseJson, field)) {
                    missingFields.add(field);
                }
            }

            if (!missingFields.isEmpty()) {
                String errorMessage = "Required fields missing: " + String.join(", ", missingFields);
                logger.error(errorMessage);
                throw new APIException(errorMessage);
            }

            logger.info("All required fields present in response");
            return true;
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            String errorMessage = "Error during required fields validation: " + e.getMessage();
            logger.error(errorMessage);
            throw new APIException(errorMessage, e);
        }
    }

    /**
     * Get a schema from cache or load it if not already cached
     *
     * @param schemaPath Path to the schema file
     * @return Schema object
     * @throws IOException If schema file cannot be read
     */
    private static Schema getSchemaFromCache(String schemaPath) throws IOException {
        if (schemaCache.containsKey(schemaPath)) {
            return schemaCache.get(schemaPath);
        }

        Schema schema = loadSchema(schemaPath);
        schemaCache.put(schemaPath, schema);
        return schema;
    }

    /**
     * Load a JSON schema from file
     *
     * @param schemaPath Path to the schema file
     * @return Schema object
     * @throws IOException If schema file cannot be read
     */
    private static Schema loadSchema(String schemaPath) throws IOException {
        try (InputStream inputStream = getResourceAsStream(schemaPath)) {
            JSONObject jsonSchema = new JSONObject(new JSONTokener(inputStream));
            return SchemaLoader.load(jsonSchema);
        }
    }

    /**
     * Load a JSON object from file
     *
     * @param filePath Path to the JSON file
     * @return JSONObject representing the file contents
     * @throws IOException If file cannot be read
     */
    private static JSONObject loadJsonFromFile(String filePath) throws IOException {
        try (InputStream inputStream = getResourceAsStream(filePath)) {
            return new JSONObject(new JSONTokener(inputStream));
        }
    }

    /**
     * Get resource as input stream, supporting both classpath and file system paths
     *
     * @param path Path to resource
     * @return InputStream for the resource
     * @throws IOException If resource cannot be found or read
     */
    private static InputStream getResourceAsStream(String path) throws IOException {
        // First try to load from classpath
        InputStream inputStream = ContractValidator.class.getClassLoader().getResourceAsStream(path);

        // If not found in classpath, try as a file path
        if (inputStream == null) {
            File file = new File(path);
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            } else {
                throw new IOException("Resource not found: " + path);
            }
        }

        return inputStream;
    }

    /**
     * Recursively collect validation errors from a ValidationException
     *
     * @param e The ValidationException
     * @param errors List to collect error messages
     */
    private static void collectValidationErrors(ValidationException e, List<String> errors) {
        errors.add(e.getMessage());
        for (ValidationException causingException : e.getCausingExceptions()) {
            collectValidationErrors(causingException, errors);
        }
    }

    /**
     * Validate a JSON object against a contract definition
     *
     * @param json The JSON object to validate
     * @param contract The contract definition
     * @param path Current JSON path (for error reporting)
     * @param errors List to collect validation errors
     */
    private static void validateJsonAgainstContract(JSONObject json, JSONObject contract, String path, List<String> errors) {
        for (String key : contract.keySet()) {
            String currentPath = path.isEmpty() ? key : path + "." + key;

            if (contract.get(key) instanceof JSONObject) {
                // If it's an object, recursively validate
                if (!json.has(key) || !(json.get(key) instanceof JSONObject)) {
                    errors.add("Missing or invalid object at path: " + currentPath);
                } else {
                    validateJsonAgainstContract(json.getJSONObject(key), contract.getJSONObject(key), currentPath, errors);
                }
            } else {
                // It's a field specification
                if (!json.has(key)) {
                    // Check if it's required
                    if (contract.get(key) instanceof String && "required".equals(contract.getString(key))) {
                        errors.add("Required field missing: " + currentPath);
                    }
                } else {
                    // Type validation
                    String expectedType = contract.getString(key);
                    if (expectedType.startsWith("type:")) {
                        String type = expectedType.substring(5);
                        if (!validateType(json.get(key), type)) {
                            errors.add("Type mismatch for field " + currentPath + ". Expected: " + type);
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if a field exists in a JSON object
     *
     * @param json The JSON object
     * @param fieldPath Path to the field (e.g., "user.address.city")
     * @return True if field exists, false otherwise
     */
    private static boolean fieldExists(JSONObject json, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        JSONObject current = json;

        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i]) || !(current.get(parts[i]) instanceof JSONObject)) {
                return false;
            }
            current = current.getJSONObject(parts[i]);
        }

        return current.has(parts[parts.length - 1]);
    }

    /**
     * Validate the type of a JSON value
     *
     * @param value The value to validate
     * @param expectedType The expected type
     * @return True if type matches, false otherwise
     */
    private static boolean validateType(Object value, String expectedType) {
        switch (expectedType.toLowerCase()) {
            case "string":
                return value instanceof String;
            case "number":
                return value instanceof Number;
            case "boolean":
                return value instanceof Boolean;
            case "object":
                return value instanceof JSONObject;
            case "array":
                return value instanceof org.json.JSONArray;
            case "null":
                return value == JSONObject.NULL;
            default:
                return false;
        }
    }
}