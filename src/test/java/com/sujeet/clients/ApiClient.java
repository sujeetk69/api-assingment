package com.sujeet.clients;

import com.sujeet.utils.LoggerUtil;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

public class ApiClient {

    private static final String BASE_URL = "https://api.restful-api.dev/objects";

    public static Response createItem(String body) {
        LoggerUtil.logger.info("Creating item with payload: {}", body);
        return given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(BASE_URL);
    }

    public static Response getItemById(String id) {
        return given().get(BASE_URL + "/" + id);
    }

    public static Response getAllItems() {
        return given().get(BASE_URL);
    }

    public static Response deleteItem(String id) {
        return given().delete(BASE_URL + "/" + id);
    }
}

