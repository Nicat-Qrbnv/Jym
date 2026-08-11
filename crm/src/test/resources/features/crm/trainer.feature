Feature: Trainer management

  Scenario: Register a new trainer successfully
    When I register a trainer with first name "Carol" and last name "Reed"
    Then the response status is 200
    And the response contains a non-blank username
    And the response contains a non-blank password

  Scenario: Register a trainer with blank last name is rejected
    When I register a trainer with first name "Carol" and last name ""
    Then the response status is 400

  Scenario: Get profile of an existing trainer
    When I get the trainer profile for username "henry.collins"
    Then the response status is 200

  Scenario: Get profile of a non-existent trainer returns 404
    When I get the trainer profile for username "no.such.trainer"
    Then the response status is 404
