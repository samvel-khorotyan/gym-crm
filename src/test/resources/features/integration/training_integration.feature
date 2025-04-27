Feature: Training Management Integration Tests
  As a gym administrator
  I want to ensure that the training management system integrates properly with other services
  So that the entire system works correctly

  Background:
    Given the training service is running
    And the user service is running
    And the system has the following users:
      | username      | firstName | lastName | userType |
      | trainee.int   | John      | Doe      | TRAINEE  |
      | trainer.int   | Jane      | Smith    | TRAINER  |
      | admin.int     | Admin     | User     | ADMIN    |
    And the system has the following training types:
      | trainingTypeName |
      | Cardio           |
      | Strength         |
      | Yoga             |
    And the trainee "trainee.int" is assigned to trainer "trainer.int"

  Scenario: Training creation updates trainer workload
    Given the user is authenticated with role "ADMIN"
    When the user sends a request to create a training with the following details:
      | traineeUsername | trainee.int |
      | trainerUsername | trainer.int |
      | trainingName    | Cardio      |
      | trainingDate    | 2023-12-15  |
      | trainingDuration| 60          |
    Then the training response status code should be 201
    And the trainer workload service should be notified about the new training
    And the trainer "trainer.int" workload should be increased by 60 minutes

  Scenario: Training update updates trainer workload
    Given the system has a training with the following details:
      | traineeUsername | trainee.int |
      | trainerUsername | trainer.int |
      | trainingName    | Yoga        |
      | trainingDate    | 2023-12-25  |
      | trainingDuration| 90          |
    And the user is authenticated with role "ADMIN"
    When the user sends a request to update the training with the following details:
      | trainingDuration| 120         |
    Then the training response status code should be 200
    And the trainer workload service should be notified about the updated training
    And the trainer "trainer.int" workload should be updated to 120 minutes

  Scenario: Training deletion updates trainer workload
    Given the system has a training with the following details:
      | traineeUsername | trainee.int |
      | trainerUsername | trainer.int |
      | trainingName    | Strength    |
      | trainingDate    | 2023-12-20  |
      | trainingDuration| 45          |
    And the user is authenticated with role "ADMIN"
    When the user sends a request to delete the training
    Then the training response status code should be 204
    And the trainer workload service should be notified about the deleted training
    And the trainer "trainer.int" workload should be decreased by 45 minutes

  Scenario: Training creation establishes trainee-trainer relationship
    Given the system has a new trainee "new.trainee" and trainer "new.trainer"
    And the trainee "new.trainee" is not assigned to trainer "new.trainer"
    And the user is authenticated with role "ADMIN"
    When the user sends a request to create a training with the following details:
      | traineeUsername | new.trainee |
      | trainerUsername | new.trainer |
      | trainingName    | Cardio      |
      | trainingDate    | 2023-12-15  |
      | trainingDuration| 60          |
    Then the training response status code should be 201
    And the trainee "new.trainee" should be assigned to trainer "new.trainer"

  Scenario: Training deletion removes trainee-trainer relationship when no more trainings exist
    Given the system has a trainee "single.trainee" and trainer "single.trainer"
    And the trainee "single.trainee" is assigned to trainer "single.trainer" with only one training
    And the user is authenticated with role "ADMIN"
    When the user sends a request to delete the training between "single.trainee" and "single.trainer"
    Then the training response status code should be 204
    And the trainee "single.trainee" should no longer be assigned to trainer "single.trainer"
