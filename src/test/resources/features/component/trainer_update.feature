Feature: Trainer Update
  As a system administrator
  I want to update trainer information
  So that their details are kept up to date

  Background:
    Given the following trainer exists in the system:
      | id            | 550e8400-e29b-41d4-a716-446655440000 |
      | firstName     | John                                 |
      | lastName      | Doe                                  |
      | specialization| Fitness                              |
      | username      | john.doe                             |
      | isActive      | true                                 |

  Scenario: Successfully update a trainer
    When I update the trainer with ID "550e8400-e29b-41d4-a716-446655440000" with the following details:
      | firstName     | John       |
      | lastName      | Smith      |
      | specialization| Yoga       |
      | isActive      | true       |
    Then the trainer should be updated successfully
    And the updated trainer should have the following details:
      | firstName     | John       |
      | lastName      | Smith      |
      | specialization| Yoga       |
      | isActive      | true       |

  Scenario: Update a non-existent trainer
    When I update the trainer with ID "550e8400-e29b-41d4-a716-446655440999" with the following details:
      | firstName     | John       |
      | lastName      | Smith      |
      | specialization| Yoga       |
      | isActive      | true       |
    Then the trainer update should fail with error "Trainer not found by trainer ID: 550e8400-e29b-41d4-a716-446655440999"

  Scenario: Deactivate a trainer
    When I update the trainer with ID "550e8400-e29b-41d4-a716-446655440000" with the following details:
      | firstName     | John       |
      | lastName      | Doe        |
      | specialization| Fitness    |
      | isActive      | false      |
    Then the trainer should be updated successfully
    And the updated trainer should have the following details:
      | firstName     | John       |
      | lastName      | Doe        |
      | specialization| Fitness    |
      | isActive      | false      |
