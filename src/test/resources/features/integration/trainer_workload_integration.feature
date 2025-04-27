Feature: Trainer Workload Integration
  As a system
  I want to ensure that trainer workload data is properly synchronized between services
  So that workload information is accurate and consistent

  Background:
    Given the trainer service is running for integration
    And the trainer workload service is running for integration
    And the following trainer exists in the system:
      | id            | 550e8400-e29b-41d4-a716-446655440000 |
      | firstName     | John                                 |
      | lastName      | Doe                                  |
      | specialization| Fitness                              |
      | username      | john.doe                             |
      | isActive      | true                                 |

  Scenario: Add training and verify workload update
    When a new training is added for trainer "john.doe" with duration 60 minutes
    And I wait for the workload to be processed
    Then the trainer workload service should receive the training data
    And the trainer's current month workload should be updated to include 60 minutes

  Scenario: Update training and verify workload update
    Given a training exists for trainer "john.doe" with duration 60 minutes
    When the training duration is updated to 90 minutes
    And I wait for the workload to be processed
    Then the trainer workload service should receive the updated training data
    And the trainer's current month workload should be updated to include 90 minutes

  Scenario: Delete training and verify workload update
    Given a training exists for trainer "john.doe" with duration 60 minutes
    When the training is deleted
    And I wait for the workload to be processed
    Then the trainer workload service should receive the deletion notification
    And the trainer's current month workload should be reduced by 60 minutes

  Scenario: Request workload data from trainer workload service
    Given the trainer "john.doe" has workload data in the trainer workload service
    When I request the current month workload for trainer "john.doe" from the trainer service
    Then the trainer service should request the data from the trainer workload service
    And the trainer service should receive the correct workload data
    And the response should contain the correct workload information
