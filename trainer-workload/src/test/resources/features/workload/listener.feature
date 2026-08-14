Feature: Trainer workload listener

  Background:
    Given the workload and index repositories are empty

  Scenario: First ADD message creates a workload document
    When I send an ADD message for trainer "john.doe" with training id 101, duration 30, date "2026-08-01"
    Then a workload document exists for trainer "john.doe"
    And the total duration for trainer "john.doe" in year 2026 month 8 is 30

  Scenario: Second ADD message accumulates duration
    When I send an ADD message for trainer "john.doe" with training id 101, duration 30, date "2026-08-01"
    And I send an ADD message for trainer "john.doe" with training id 102, duration 45, date "2026-08-10"
    Then the total duration for trainer "john.doe" in year 2026 month 8 is 75

  Scenario: Duplicate ADD with same training id is idempotent
    When I send an ADD message for trainer "john.doe" with training id 102, duration 45, date "2026-08-10"
    And I send an ADD message for trainer "john.doe" with training id 102, duration 45, date "2026-08-10"
    Then the total duration for trainer "john.doe" in year 2026 month 8 is 45

  Scenario: DELETE after ADD subtracts duration and marks index as DELETED
    When I send an ADD message for trainer "john.doe" with training id 101, duration 30, date "2026-08-01"
    And I send an ADD message for trainer "john.doe" with training id 102, duration 45, date "2026-08-10"
    And I send a DELETE message for trainer "john.doe" with training id 101, duration 30, date "2026-08-01"
    Then the total duration for trainer "john.doe" in year 2026 month 8 is 45
    And the index state for training id 101 is "DELETED"

  Scenario: DELETE for unknown training id at max delivery count routes to DLQ
    When I send a DELETE message at max delivery attempts for training id 999, duration 60, date "2026-08-01"
    Then the DLQ contains a dead-letter message for training id 999
