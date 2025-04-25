Feature: Training Management Component Tests
  As a gym administrator
  I want to manage training sessions
  So that I can organize workouts for trainees with trainers

  Background:
    Given the system has the following users:
      | username      | firstName | lastName | userType |
      | trainee.user  | John      | Doe      | TRAINEE  |
      | trainer.user  | Jane      | Smith    | TRAINER  |
      | admin.user    | Admin     | User     | ADMIN    |
    And the system has the following training types:
      | trainingTypeName |
      | Cardio           |
      | Strength         |
      | Yoga             |
    And the trainee "trainee.user" is assigned to trainer "trainer.user"

  Scenario: Admin creates a new training session successfully
    Given the user is authenticated with role "ADMIN"
    When the user sends a request to create a training with the following details:
      | traineeUsername | trainee.user |
      | trainerUsername | trainer.user |
      | trainingName    | Cardio       |
      | trainingDate    | 2023-12-15   |
      | trainingDuration| 60           |
    Then the response status code should be 201
    And the response should contain a training with name "Cardio"
    And the response should contain a training with trainee "trainee.user"
    And the response should contain a training with trainer "trainer.user"

  Scenario: Non-admin user attempts to create a training session
    Given the user is authenticated with role "TRAINEE"
    When the user sends a request to create a training with the following details:
      | traineeUsername | trainee.user |
      | trainerUsername | trainer.user |
      | trainingName    | Cardio       |
      | trainingDate    | 2023-12-15   |
      | trainingDuration| 60           |
    Then the response status code should be 403

  Scenario: Get training by ID
    Given the system has a training with the following details:
      | traineeUsername | trainee.user |
      | trainerUsername | trainer.user |
      | trainingName    | Strength     |
      | trainingDate    | 2023-12-20   |
      | trainingDuration| 45           |
    When the user sends a request to get the training by ID
    Then the response status code should be 200
    And the response should contain a training with name "Strength"
    And the response should contain a training with trainee "trainee.user"
    And the response should contain a training with trainer "trainer.user"

  Scenario: Admin updates a training session successfully
    Given the system has a training with the following details:
      | traineeUsername | trainee.user |
      | trainerUsername | trainer.user |
      | trainingName    | Yoga         |
      | trainingDate    | 2023-12-25   |
      | trainingDuration| 90           |
    And the user is authenticated with role "ADMIN"
    When the user sends a request to update the training with the following details:
      | trainingName    | Cardio       |
      | trainingDate    | 2023-12-26   |
      | trainingDuration| 75           |
    Then the response status code should be 200
    And the response should contain a training with name "Cardio"
    And the response should contain a training with date "2023-12-26"
    And the response should contain a training with duration 75

  Scenario: Non-admin user attempts to update a training session
    Given the system has a training with the following details:
      | traineeUsername | trainee.user |
      | trainerUsername | trainer.user |
      | trainingName    | Yoga         |
      | trainingDate    | 2023-12-25   |
      | trainingDuration| 90           |
    And the user is authenticated with role "TRAINER"
    When the user sends a request to update the training with the following details:
      | trainingName    | Cardio       |
      | trainingDate    | 2023-12-26   |
      | trainingDuration| 75           |
    Then the response status code should be 403

  Scenario: Admin deletes a training session successfully
    Given the system has a training with the following details:
      | traineeUsername | trainee.user |
      | trainerUsername | trainer.user |
      | trainingName    | Cardio       |
      | trainingDate    | 2023-12-15   |
      | trainingDuration| 60           |
    And the user is authenticated with role "ADMIN"
    When the user sends a request to delete the training
    Then the response status code should be 204
    And the training should no longer exist in the system

  Scenario: Non-admin user attempts to delete a training session
    Given the system has a training with the following details:
      | traineeUsername | trainee.user |
      | trainerUsername | trainer.user |
      | trainingName    | Cardio       |
      | trainingDate    | 2023-12-15   |
      | trainingDuration| 60           |
    And the user is authenticated with role "TRAINEE"
    When the user sends a request to delete the training
    Then the response status code should be 403
