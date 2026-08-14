Feature: Authentication

  Background:
    Given the brute force counter is reset for user "oliver.bennett"

  Scenario: Successful login returns a JWT token
    When I log in with username "oliver.bennett" and password "A7mQ2zLp9R"
    Then the response status is 200
    And the response contains a non-blank JWT token
    And the token subject is "oliver.bennett"

  Scenario: Login with wrong password is rejected
    When I log in with username "oliver.bennett" and password "wrong-password"
    Then the response status is 401

  Scenario: Account is blocked after three failed login attempts
    When I log in with username "oliver.bennett" and password "wrong-password"
    And I log in with username "oliver.bennett" and password "wrong-password"
    And I log in with username "oliver.bennett" and password "wrong-password"
    And I log in with username "oliver.bennett" and password "wrong-password"
    Then the response status is 401
    And the response body contains "temporarily blocked"
