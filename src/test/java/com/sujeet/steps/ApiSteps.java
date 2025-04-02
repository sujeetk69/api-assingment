package com.sujeet.steps;

import com.sujeet.clients.ApiClient;
import com.sujeet.exceptions.UnsupportedActionException;
import com.sujeet.utils.LoggerUtil;
import io.cucumber.cienvironment.internal.com.eclipsesource.json.JsonObject;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Objects;

public class ApiSteps {

    private JsonObject payload;
    private Response response;
    private String createdItemId;

    public ApiSteps() {
        // Define payload and empty data in constructor to avoid checking in further steps
        payload = new JsonObject();
        payload.add("data", new JsonObject());
    }

    @Given("a {string} item is created")
    public void addName(String itemName) {
        payload.add("name", itemName);
    }

    @Given("is a {string} CPU model")
    public void addCpuModel(String cpuModel) {
        payload.get("data").asObject().add("CPU Model", cpuModel);
    }

    @Given("has a price of {float}")
    public void addPrice(float price) {
        payload.get("data").asObject().add("price", price);
    }

    @Given("an item with name {string} exists")
    public void createItem(String itemName) {
        addName(itemName);
        addPrice(1299);
        addCpuModel("Intel Core i5");
        sendCreateItemRequest();
        verifyStatusCode(200);
    }

    @Given("an id which does not exist")
    public void setNonExistingId() {
        // May be generated a random id, or delete first, for not I am just hardcoding
        createdItemId = "lskdjfnadlskjfnqpwdonqwdjfn";
    }

    @Given("an invalid payload to add item")
    public void createInvalidPayload() {
        payload = null;
    }

    @When("the request to add the item is made")
    public void sendCreateItemRequest() {
        if (payload != null) {
            response = ApiClient.createItem(payload.toString());
        } else {
            // to reproduce negative scenario
            response = ApiClient.createItem("");
        }
        LoggerUtil.logger.info("Create Item Response: {}", response);
        createdItemId = response.jsonPath().getString("id");
    }

    @When("a request to {string} the item is sent")
    public void sendGetOrDeleteItemRequest(String action) throws UnsupportedActionException {
        if (createdItemId == null) {
            LoggerUtil.logger.error("Item is not yet created, please create an item in test setup");
        }
        if (Objects.equals(action, "retrieve")) {
            response = ApiClient.getItemById(createdItemId);
        } else if (Objects.equals(action, "delete")) {
            response = ApiClient.deleteItem(createdItemId);
        } else {
            throw new UnsupportedActionException("Unsupported action: " + action);
        }
        LoggerUtil.logger.info("{} Item Response: {}", action, response);
    }

    @When("a request to retrieve all items is sent")
    public void sendGetAllItemsRequest() {
        response = ApiClient.getAllItems();
        LoggerUtil.logger.info("GET All Items Response: {}", response);
    }

    @Then("a {int} response code is returned")
    public void verifyStatusCode(int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        LoggerUtil.logger.info("Actual Status Code: {}", actualStatusCode);
        LoggerUtil.logger.info("Expected Status Code: {}", expectedStatusCode);
        Assert.assertEquals(actualStatusCode, expectedStatusCode, "Response status code is not as expected");
    }

    @Then("the response should contain the correct object details")
    public void verifyObjectDetails() {
        Assert.assertNotNull(response.jsonPath().getString("name"), "Object name is missing!");
    }

    @Then("an item with name {string} is successfully created")
    public void itemCreationSuccessful(String expectedItemName) {
        validateItemDetails(expectedItemName);
    }

    @Then("the response item should have name {string}")
    public void validateItemDetails(String expectedItemName) {
        String actualItemName = response.jsonPath().getString("name");
        LoggerUtil.logger.info("Actual Item Name: {}", actualItemName);
        LoggerUtil.logger.info("Expected Item Name: {}", expectedItemName);
        Assert.assertEquals(actualItemName, expectedItemName, "Item name is not as expected");
    }

    @Then("the response is a non empty list of items")
    public void validateListNotEmpty() {
        int itemsCount = response.jsonPath().getList("$").size();
        LoggerUtil.logger.info("Received Items Count: {}", itemsCount);
        Assert.assertTrue(itemsCount > 0, "Item is count is 0");
    }

    @Then("response has error message for delete request")
    public void verifyDeleteNotFoundErrorMessage() {
        String expectedErrorMessage = "Object with id = " + createdItemId + " doesn't exist.";
        String actualErrorMessage = response.jsonPath().getString("error");
        Assert.assertEquals(actualErrorMessage, expectedErrorMessage, "Error message is not correct");
    }

    @Then("response has error message for get request")
    public void verifyGetNotFoundErrorMessage() {
        // This failed initially because actual error message has a typo "Oject" instead of "Object", A defect maybe :D
        String expectedErrorMessage = "Oject with id=" + createdItemId + " was not found.";
        String actualErrorMessage = response.jsonPath().getString("error");
        Assert.assertEquals(actualErrorMessage, expectedErrorMessage, "Error message is not correct");
    }

    public void verifySuccessfullyDeletedErrorMessage() {
        String expectedMessage = "Object with id = " + createdItemId + " has been deleted.";
        String actualMessage = response.jsonPath().getString("message");
        Assert.assertEquals(actualMessage, expectedMessage, "Message is not correct");
    }

    @Then("the item is deleted successfully")
    public void verifyDeleteSuccess() {
        // Verify the delete response message
        verifySuccessfullyDeletedErrorMessage();

        // Now to be double sure, let's try to retrieve it
        try {
            sendGetOrDeleteItemRequest("retrieve");
        } catch (UnsupportedActionException e) {
            throw new RuntimeException(e);
        }
        verifyStatusCode(404);
        verifyGetNotFoundErrorMessage();
    }


}
