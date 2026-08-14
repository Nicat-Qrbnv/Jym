Feature: Trainee management

  Scenario: Register a new trainee successfully
    When I register a trainee with first name "Alice" and last name "Walker"
    Then the response status is 200
    And the response contains a non-blank username
    And the response contains a non-blank password

  Scenario: Register a trainee with blank first name is rejected
    When I register a trainee with first name "" and last name "Walker"
    Then the response status is 400

  Scenario: Get profile of an existing trainee
    Given a trainee exists with first name "Bob" and last name "Stone"
    When I get the trainee profile for that trainee
    Then the response status is 200
    And the response contains first name "Bob" and last name "Stone"

  Scenario: Get profile of a non-existent trainee returns 404
    When I get the trainee profile for username "no.such.trainee"
    Then the response status is 404
