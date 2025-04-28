package com.prasad_v.tests.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserApiGetTest {

    private RequestSpecification reqSpec;
    private ResponseSpecification respSpec;

    @BeforeClass
    public void setup() {
        // Configure RestAssured base URI (e.g., from config)
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
        // Build a reusable request spec: JSON content type, logging, etc.
        reqSpec = new RequestSpecBuilder()
                .setContentType("application/json")
                .log(io.restassured.filter.log.LogDetail.ALL)
                .build();
        // Build a reusable response spec: expect 200 OK and JSON content type
        respSpec = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType("application/json; charset=utf-8")
                .build();
    }

    @Test(description = "Verify GET /users returns user with id=1")
    public void getUserById() {
        given()
                .spec(reqSpec)
                .queryParam("id", 1)               // set query parameter ?id=1
                .when()
                .get("/users")                     // perform GET request
                .then()
                .spec(respSpec)                    // apply response spec
                .body("[0].username", equalTo("Bret"));  // assert first element’s username
    }
}
