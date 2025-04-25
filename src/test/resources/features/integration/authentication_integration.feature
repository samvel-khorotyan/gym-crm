Feature: Authentication Controller Integration Tests
  As a user of the gym CRM system
  I want to authenticate, update my password, and logout
  So that I can securely access the system

  Background:
    Given the system has a user with the following details:
      | firstName | lastName | password    | userType |
      | Test      | User     | Password123 | TRAINEE  |

  Scenario: Successful login with valid credentials
    When the client sends a POST request to "/users/me/login" with:
      | username | test.user   |
      | password | Password123 |
    Then the response status code should be 200
    And the response should contain a valid JWT token

  Scenario: Failed login with invalid password
    When the client sends a POST request to "/users/me/login" with:
      | username | testuser    |
      | password | wrongpass   |
    Then the response status code should be 401
    And the response should contain an error message "Invalid username or password."

  Scenario: Failed login with non-existent user
    When the client sends a POST request to "/users/me/login" with:
      | username | nonexistent |
      | password | Password123 |
    Then the response status code should be 401
    And the response should contain an error message "Invalid username or password."

  Scenario: Successful password update
    Given the client is authenticated with username "testuser"
    When the client sends a PUT request to "/users/me/authentication" with:
      | username    | testuser    |
      | oldPassword | Password123 |
      | newPassword | NewPass456  |
    Then the response status code should be 200

  Scenario: Failed password update with incorrect old password
    Given the client is authenticated with username "testuser"
    When the client sends a PUT request to "/users/me/authentication" with:
      | username    | testuser    |
      | oldPassword | wrongpass   |
      | newPassword | NewPass456  |
    Then the response status code should be 400
    And the response should contain an error message "Current password is incorrect"

  Scenario: Successful logout
    Given the client is authenticated with username "testuser"
    When the client sends a POST request to "/users/me/logout"
    Then the response status code should be 200
    And the token should be blacklisted

  Scenario: Accessing protected resource after logout
    Given the client is authenticated with username "testuser"
    And the client has logged out
    When the client tries to access a protected resource
    Then the response status code should be 401