Feature: Trainer Creation
  As a system administrator
  I want to create trainers in the system
  So that they can be assigned to trainees

  Background:
    Given the system is ready to create trainers

  Scenario: Successfully create a trainer
    When I create a trainer with the following details:
      | firstName     | John       |
      | lastName      | Doe        |
      | specialization| Fitness    |
    Then the trainer should be created successfully
    And the trainer should have username and password
    And the trainer should have the following details:
      | firstName     | John       |
      | lastName      | Doe        |
      | specialization| Fitness    |
      | isActive      | true       |

  Scenario: Create a trainer with missing required fields
    When I create a trainer with the following details:
      | firstName     |            |
      | lastName      | Doe        |
      | specialization| Fitness    |
    Then the trainer creation should fail with error "First name is required"

  Scenario: Create a trainer with invalid data
    When I create a trainer with the following details:
      | firstName     | John123    |
      | lastName      | Doe        |
      | specialization| Fitness    |
    Then the trainer creation should fail with error "First name should contain only letters"
