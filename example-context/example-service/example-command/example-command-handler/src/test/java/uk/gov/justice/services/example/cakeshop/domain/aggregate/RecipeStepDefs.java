package uk.gov.justice.services.example.cakeshop.domain.aggregate;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import uk.gov.justice.services.example.cakeshop.domain.Ingredient;
import uk.gov.justice.services.example.cakeshop.domain.event.RecipeAdded;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.mock;

public class RecipeStepDefs {
    private static final UUID RECIPE_ID = UUID.randomUUID();
    private static final Ingredient INGREDIENT = mock(Ingredient.class);
    private static final List<Ingredient> INGREDIENTS = singletonList(INGREDIENT);
    private Stream<Object> events;

    private Recipe recipe;

    @Given("the recipe management system is up and running")
    public void system_is_up_and_running() {
        this.recipe = new Recipe();
    }

    @When("the manager adds a new recipe with id (.*) and name (.*) in the system")
    public void add_new_receipe(String recipeId, String recipeName) {
        events = this.recipe.addRecipe(UUID.fromString(recipeId), recipeName, true, INGREDIENTS);
    }

    @Then("the event is generated and new recipe with id (.*) and name (.*) should be stored in the system")
    public void new_recipe_event_generated(String recipeId, String recipeName) {
        final List<Object> eventList = events.collect(toList());
        assertThat(eventList, hasSize(1));

        final Object event = eventList.get(0);
        assertThat(event, instanceOf(RecipeAdded.class));

        final RecipeAdded recipeAdded = (RecipeAdded) event;
        assertThat(recipeAdded.getRecipeId(), equalTo(UUID.fromString(recipeId)));
        assertThat(recipeAdded.getName(), equalTo(recipeName));
        assertThat(recipeAdded.getIngredients(), equalTo(INGREDIENTS));
        assertThat(recipeAdded.isGlutenFree(), is(true));
    }
}
