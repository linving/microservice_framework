package uk.gov.justice.services.example.cakeshop.domain.aggregate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import uk.gov.justice.services.example.cakeshop.domain.Ingredient;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static java.lang.System.out;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.mock;

public class RecipeStepDefs {
    private static final UUID RECIPE_ID = UUID.randomUUID();
    private Stream<Object> events;
    private Class<?> clazz;
    private Object object;
    private static final String fmt = "%24s: %s%n";
    private static final Ingredient INGREDIENT = mock(Ingredient.class);
    private static final List<Ingredient> INGREDIENTS = singletonList(INGREDIENT);

    @Given("no previous events")
    public void no_previous_events() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        assert events == null;
    }

    @When("the method (.*) is called on the aggregate (.*)")
    public void add_new_receipe_2(final String methodName, String aggregate, final String message) throws Exception {

        checkIfAggregateCreated(aggregate);

        Class<?>[] pType = paramTypes(methodName);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualNode = mapper.readTree(message);

        Map argumentsMap = mapper.convertValue(actualNode, Map.class);
        List valuesList = new ArrayList(argumentsMap.values());
        checkIfUUID(valuesList);
        Object[] methodArgs = valuesList.toArray();
        Method method = object.getClass().getMethod(methodName, pType);
        if (argumentsMap.size() == 0) {
            events = (Stream<Object>) method.invoke(object, null);
        } else {
            events = (Stream<Object>) method.invoke(object, methodArgs);
        }
    }

    private void checkIfAggregateCreated(String aggregate) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (clazz == null && object == null) {
            this.clazz = Class.forName(aggregate);
            this.object = clazz.newInstance();
        }
    }

    private Class<?>[] paramTypes(String methodName) {
        Method[] allMethods = clazz.getDeclaredMethods();
        Class<?>[] pType = null;
        for (Method m : allMethods) {
            if (!m.getName().equals(methodName)) {
                continue;
            }

            pType = m.getParameterTypes();
            Type[] gpType = m.getGenericParameterTypes();
            for (int i = 0; i < pType.length; i++) {
                out.format(fmt, "ParameterType", pType[i]);
                out.format(fmt, "GenericParameterType", gpType[i]);
            }

        }
        return pType;
    }

    private void checkIfUUID(List argumentValues) {
        for (int index = 0; index < argumentValues.size(); index++) {
            try {
                if (argumentValues.get(index) instanceof String) {
                    UUID uuid = UUID.fromString((String) argumentValues.get(index));
                    argumentValues.remove(index);
                    argumentValues.add(index, uuid);
                }
            } catch (IllegalArgumentException exception) {
                continue;
            }
        }
    }

    private List<Ingredient> getIngredients(JsonNode actualNode) throws com.fasterxml.jackson.core.JsonProcessingException {

        JsonNode ingredients = actualNode.get("ingredients");
        List<Ingredient> ingredientList = new ArrayList<>();
        if (ingredients != null) {
            for (JsonNode jNode : ingredients) {
                ingredientList.add(new Ingredient(jNode.get("name").asText(), jNode.get("quantity").asInt()));
            }
        }
        return ingredientList;
    }

    @Then("the following events are generated")
    public void new_recipe_event_generated(List<String> expectedEvents) throws ClassNotFoundException {
        final List<Object> eventList = events.collect(toList());
        assertThat(eventList, hasSize(1));
        final Object event = eventList.get(0);
        for (int index = 0; index < expectedEvents.size(); index++) {
            assertThat(event, instanceOf(Class.forName(expectedEvents.get(index))));
        }


//        final RecipeAdded recipeAdded = (RecipeAdded) event;
//        assertThat(recipeAdded.getRecipeId(), equalTo(UUID.fromString(recipeId)));
//        assertThat(recipeAdded.getName(), equalTo(recipeName));
//        assertThat(recipeAdded.getIngredients(), equalTo(INGREDIENTS));
//        assertThat(recipeAdded.isGlutenFree(), is(true));
    }
}
