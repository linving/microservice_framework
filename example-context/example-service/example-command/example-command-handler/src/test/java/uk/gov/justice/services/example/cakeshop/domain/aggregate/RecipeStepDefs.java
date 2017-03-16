package uk.gov.justice.services.example.cakeshop.domain.aggregate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import uk.gov.justice.services.example.cakeshop.domain.Ingredient;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.intThat;
import static org.mockito.Mockito.mock;

public class RecipeStepDefs {
    private static final UUID RECIPE_ID = UUID.randomUUID();
    private static final Ingredient INGREDIENT = mock(Ingredient.class);
    private static final List<Ingredient> INGREDIENTS = singletonList(INGREDIENT);
    private Stream<Object> events;
    private Class<?> clazz;
    private Object object;

    @Given("no previous events")
    public void no_previous_events() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        assert events == null;
    }

    @When("the aggregate (.*) is created")
    public void create_aggregate_object(String aggregate) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        this.clazz = Class.forName(aggregate);
        this.object = clazz.newInstance();
    }

    @When("the method (.*) is called with following json")
    public void add_new_receipe_2(final String methodName, final String message) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        System.out.println(message);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualNode = mapper.readTree(message);

        Map<String, Object> argsValues = new HashMap<>();

        if (actualNode.get("argsValues") != null) {
            argsValues = mapper.convertValue(actualNode.get("argsValues"), Map.class);
        }

        List argumentValues = new ArrayList(argsValues.values());
        Class<?>[] argsTypesResult = argsTypes(new ArrayList(argsValues.keySet()), argumentValues);
        Object[] methodArgs = argumentValues.toArray();
        Method method = object.getClass().getMethod(methodName, argsTypesResult);
        if (argsValues.size() == 0) {
            events = (Stream<Object>) method.invoke(object, null);
        } else {
            events = (Stream<Object>) method.invoke(object, methodArgs);
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


    private Class<?>[] argsTypes(List argsTypesIn, List argsValues) {
        Class<?>[] result = new Class[argsTypesIn.size()];
        for (int i = 0; i < argsTypesIn.size(); i++) {
            switch (((String) argsTypesIn.get(i)).trim()) {
                case "string":
                    result[i] = String.class;
                    break;
                case "uuid":
                    UUID uuid = UUID.fromString((String) argsValues.get(i));
                    argsValues.remove(i);
                    argsValues.add(i, uuid);
                    result[i] = UUID.class;
                    break;
                case "boolean":
                    boolean bol = (Boolean) argsValues.get(i);
                    argsValues.remove(i);
                    argsValues.add(i, bol);
                    result[i] = Boolean.class;
                    break;
                case "list":
                    List list = new ArrayList(((HashMap) argsValues.get(i)).values());
                    argsValues.remove(i);
                    argsValues.add(i, list);
                    result[i] = List.class;
                    break;
            }
        }
        return result;
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
