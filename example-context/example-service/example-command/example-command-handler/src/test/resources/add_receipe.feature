Feature: Recipe Management

  Scenario Outline: Add a new recipe
    Given the recipe management system is up and running
    When the manager adds a new recipe with id <recipeId> and name <recipeName> in the system
    Then the event is generated and new recipe with id <recipeId> and name <recipeName> should be stored in the system

    Examples:
      | recipeId                             | recipeName  |
      | 5c5a1d30-0414-11e7-93ae-92361f002671 | cheese cake |
      | 682ff5ee-0414-11e7-93ae-92361f002671 | apple pie   |
