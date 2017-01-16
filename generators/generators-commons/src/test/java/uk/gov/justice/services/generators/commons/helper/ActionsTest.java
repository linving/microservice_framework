package uk.gov.justice.services.generators.commons.helper;

import static java.util.Collections.EMPTY_LIST;
import static net.trajano.commons.testing.UtilityClassTestUtil.assertUtilityClassWellDefined;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.DELETE;
import static org.raml.model.ActionType.GET;
import static org.raml.model.ActionType.HEAD;
import static org.raml.model.ActionType.OPTIONS;
import static org.raml.model.ActionType.PATCH;
import static org.raml.model.ActionType.POST;
import static org.raml.model.ActionType.PUT;
import static org.raml.model.ActionType.TRACE;
import static uk.gov.justice.services.generators.commons.helper.Actions.hasResponseMimeTypes;
import static uk.gov.justice.services.generators.commons.helper.Actions.isSupportedActionType;
import static uk.gov.justice.services.generators.commons.helper.Actions.isSupportedActionTypeWithRequestType;
import static uk.gov.justice.services.generators.commons.helper.Actions.isSupportedActionTypeWithResponseTypeOnly;
import static uk.gov.justice.services.generators.commons.helper.Actions.responseMimeTypesOf;
import static uk.gov.justice.services.generators.test.utils.builder.HttpActionBuilder.httpAction;

import org.junit.Test;
import org.raml.model.Action;

public class ActionsTest {

    @Test
    public void shouldBeWellDefinedUtilityClass() {
        assertUtilityClassWellDefined(Actions.class);
    }

    @Test
    public void shouldReturnEmptyListForActionContainingNullResponses() throws Exception {
        final Action action = httpAction().build();
        assertThat(responseMimeTypesOf(action), equalTo(EMPTY_LIST));
    }

    @Test
    public void shouldReturnEmptyListForActionContainingNullValueInResponses() throws Exception {
        final Action action = httpAction().withNullResponseType().build();
        assertThat(responseMimeTypesOf(action), equalTo(EMPTY_LIST));
    }

    @Test
    public void shouldReturnEmptyListForActionContainingResponseAndNoBodyType() throws Exception {
        final Action action = httpAction()
                .withHttpActionResponseAndNoBody()
                .build();
        assertThat(responseMimeTypesOf(action), equalTo(EMPTY_LIST));
    }

    @Test
    public void shouldReturnEmptyListForActionContainingResponseAndNullBodyType() throws Exception {
        final Action action = httpAction()
                .withHttpActionResponseAndEmptyBody()
                .build();
        assertThat(responseMimeTypesOf(action), equalTo(EMPTY_LIST));
    }

    @Test
    public void shouldReturnListOfBodyTypesForActionContainingResponseAndBodyType() throws Exception {
        final Action action = httpAction()
                .withResponseTypes("application/json")
                .build();
        assertThat(responseMimeTypesOf(action).iterator().next().getType(), equalTo("application/json"));
    }

    @Test
    public void shouldReturnTrueIfActionHasResponseTypes() throws Exception {
        final Action action = httpAction()
                .withResponseTypes("application/json")
                .build();
        assertThat(hasResponseMimeTypes(action), equalTo(true));
    }

    @Test
    public void shouldReturnFalseIfActionDoesNotHaveResponseTypes() throws Exception {
        final Action action = httpAction().build();
        assertThat(hasResponseMimeTypes(action), equalTo(false));
    }

    @Test
    public void shouldReturnTrueIfSupportedActionType() throws Exception {
        assertThat(isSupportedActionType(DELETE), equalTo(true));
        assertThat(isSupportedActionType(GET), equalTo(true));
        assertThat(isSupportedActionType(PATCH), equalTo(true));
        assertThat(isSupportedActionType(POST), equalTo(true));
        assertThat(isSupportedActionType(PUT), equalTo(true));
    }

    @Test
    public void shouldReturnFalseIfUnsupportedActionType() throws Exception {
        assertThat(isSupportedActionType(OPTIONS), equalTo(false));
        assertThat(isSupportedActionType(HEAD), equalTo(false));
        assertThat(isSupportedActionType(TRACE), equalTo(false));
    }

    @Test
    public void shouldReturnTrueIfSupportedActionTypeWithRequestType() throws Exception {
        assertThat(isSupportedActionTypeWithRequestType(DELETE), equalTo(true));
        assertThat(isSupportedActionTypeWithRequestType(PATCH), equalTo(true));
        assertThat(isSupportedActionTypeWithRequestType(POST), equalTo(true));
        assertThat(isSupportedActionTypeWithRequestType(PUT), equalTo(true));
    }

    @Test
    public void shouldReturnFalseIfNotSupportedActionTypeWithRequestType() throws Exception {
        assertThat(isSupportedActionTypeWithRequestType(GET), equalTo(false));
    }

    @Test
    public void shouldReturnTrueIfSupportedActionTypeWithResponseTypeOnly() throws Exception {
        assertThat(isSupportedActionTypeWithResponseTypeOnly(GET), equalTo(true));
    }

    @Test
    public void shouldReturnFalseIfNotSupportedActionTypeWithResponseTypeOnly() throws Exception {
        assertThat(isSupportedActionTypeWithResponseTypeOnly(DELETE), equalTo(false));
        assertThat(isSupportedActionTypeWithResponseTypeOnly(PATCH), equalTo(false));
        assertThat(isSupportedActionTypeWithResponseTypeOnly(POST), equalTo(false));
        assertThat(isSupportedActionTypeWithResponseTypeOnly(PUT), equalTo(false));
    }
}