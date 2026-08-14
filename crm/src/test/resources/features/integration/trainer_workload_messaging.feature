Feature: CRM to trainer-workload messaging contract

  Background:
    Given the JMS queue is drained

  Scenario: Creating a training publishes one ADD message with correct fields
    When I create a training with name "Strength Session", trainee "oliver.bennett", trainer "henry.collins", date "2026-09-01", duration 60
    Then one message is published to the workload queue
    And the message action type is "ADD"
    And the message trainer username is "henry.collins"
    And the message duration is 60
    And the message training date is "2026-09-01"

  Scenario: Deleting a trainee with two trainings publishes two DELETE messages
    Given a trainee exists with first name "Bob" and last name "Stone"
    And that trainee has a training with trainer "henry.collins", date "2026-09-01", duration 30
    And that trainee has a training with trainer "henry.collins", date "2026-09-02", duration 45
    And the JMS queue is drained
    When I delete that trainee
    Then exactly 2 messages are published to the workload queue
    And all messages have action type "DELETE"

  Scenario: Training creation with invalid input publishes no message
    When I create a training with name "", trainee "oliver.bennett", trainer "henry.collins", date "2026-09-01", duration 30
    Then no message is published to the workload queue
