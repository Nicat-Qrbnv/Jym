Feature: Training management

  Scenario: Create a training with valid data succeeds
    When I create a training with name "Strength Session", trainee "oliver.bennett", trainer "henry.collins", date "2026-09-01", duration 60
    Then the response status is 200

  Scenario: Create a training with unknown trainee returns 404
    When I create a training with name "Cardio", trainee "no.such.trainee", trainer "henry.collins", date "2026-09-01", duration 30
    Then the response status is 404

  Scenario: Create a training with blank name is rejected
    When I create a training with name "", trainee "oliver.bennett", trainer "henry.collins", date "2026-09-01", duration 30
    Then the response status is 400

  Scenario: Create a training with zero duration is rejected
    When I create a training with name "Yoga", trainee "oliver.bennett", trainer "henry.collins", date "2026-09-01", duration 0
    Then the response status is 400
