Feature: Authentication functionality
  As a user
  I want to be able to login and logout
  So that I can access protected resources

  Scenario: Successful login with valid credentials
    Given a user exists with username "user1" and password "password123"
    When the user attempts to login with username "user1" and password "password123"
    Then the login should be successful
    And a valid JWT token should be returned

  Scenario: Failed login with invalid password
    Given a user exists with username "user1" and password "password123"
    When the user attempts to login with username "user1" and password "wrongpassword"
    Then the login should fail with status code 401
    And the response should contain error message "Invalid username or password."

  Scenario: Failed login with non-existent user
    When the user attempts to login with username "nonexistentuser" and password "password123"
    Then the login should fail with status code 401
    And the response should contain error message "Invalid username or password."

  Scenario: Account gets locked after multiple failed login attempts
    Given a user exists with username "user2" and password "password123"
    When the user attempts to login with username "user2" and password "wrongpassword" 5 times
    And the user attempts to login with username "user2" and password "password123"
    Then the login should fail with status code 403
    And the response should contain error message "User is blocked for 5 minutes due to multiple failed login attempts."

  Scenario: Successful password change
    Given a user exists with username "user3" and password "oldpassword"
    And the user is authenticated with username "user3"
    When the user attempts to change password from "oldpassword" to "newpassword"
    Then the password change should be successful

  Scenario: Failed password change with incorrect old password
    Given a user exists with username "user3" and password "oldpassword"
    And the user is authenticated with username "user3"
    When the user attempts to change password from "wrongoldpassword" to "newpassword"
    Then the password change should fail with status code 400
    And the response should contain error message "Current password is incorrect"

  Scenario: Successful logout
    Given a user exists with username "user4" and password "password123"
    And the user is authenticated with username "user4"
    When the user attempts to logout
    Then the logout should be successful
    And the token should be blacklisted
