Feature: API Testing for Objects

  # Positive Scenarios
  Scenario: Verify an item can be created
    Given a "Apple MacBook Pro 16" item is created
    And is a "Intel Core i9" CPU model
    And has a price of 1849.99
    When the request to add the item is made
    Then a 200 response code is returned
    And an item with name "Apple MacBook Pro 16" is successfully created

  Scenario: Verify an existing item can be retrieved
    Given an item with name "Lenovo Think pad" exists
    When a request to "retrieve" the item is sent
    Then a 200 response code is returned
    And the response item should have name "Lenovo Think pad"

  Scenario: Verify an existing item can be deleted
    Given an item with name "HP Envy" exists
    When a request to "delete" the item is sent
    Then a 200 response code is returned
    And the item is deleted successfully

  Scenario: Verify multiple items can be retrieved
    When a request to retrieve all items is sent
    Then a 200 response code is returned
    And the response is a non empty list of items

  # Few Negative Scenarios
  Scenario: Verify retrieving a non existing item gives error response
    Given an id which does not exist
    When a request to "retrieve" the item is sent
    Then a 404 response code is returned
    And response has error message for get request

  Scenario: Verify deleting a non existing item gives error response
    Given an id which does not exist
    When a request to "delete" the item is sent
    Then a 404 response code is returned
    And response has error message for delete request

  Scenario: Verify adding an item with invalid payload gives error response
    Given an invalid payload to add item
    When the request to add the item is made
    Then a 400 response code is returned


  # Edge cases
  Scenario: Verify adding an item with name exactly 255 characters is successful
    Given a "MyPDssssssssssssssssssssssssssssswdgwePDssssPDsssssssssssssssPDssssssssssssssssssssssssssssswdgwergweeeeeeeeeesssssssssssssswdgwergweeeeeeeeeessssssssssssssssssssssssswdgwergweeeeeeeeeergweeeeeeeeeePDssPDssssssssssssssssssssssssssssswdgwergweeeeeeeeessess" item is created
    When the request to add the item is made
    Then a 200 response code is returned

  Scenario: Verify adding an item with name more than 255 characters gives error response
    Given a "MyPDssssssssssssssssssssssssssssswdgwePDssssPDsssssssssssssssPDssssssssssssssssssssssssssssswdgwergweeeeeeeeeesssssssssssssswdgwergweeeeeeeeeessssssssssssssssssssssssswdgwergweeeeeeeeeergweeeeeeeeeePDssPDssssssssssssssssssssssssssssswdgwergweeeeeeeeessesss" item is created
    When the request to add the item is made
    Then a 500 response code is returned
