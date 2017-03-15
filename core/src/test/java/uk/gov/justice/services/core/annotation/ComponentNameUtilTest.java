package uk.gov.justice.services.core.annotation;


import static net.trajano.commons.testing.UtilityClassTestUtil.assertUtilityClassWellDefined;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_CONTROLLER;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_HANDLER;
import static uk.gov.justice.services.core.annotation.Component.EVENT_LISTENER;
import static uk.gov.justice.services.core.annotation.Component.QUERY_API;
import static uk.gov.justice.services.core.annotation.ComponentNameUtil.componentFrom;

import uk.gov.justice.services.test.utils.common.MemberInjectionPoint;

import javax.inject.Inject;

import org.junit.Test;

public class ComponentNameUtilTest {

    private static final String FIELD_NAME = "field";

    @Test
    public void shouldBeWellDefinedUtilityClass() {
        assertUtilityClassWellDefined(ComponentNameUtil.class);
    }

    @Test
    public void shouldReturnFieldLevelComponent() throws NoSuchFieldException {
        assertThat(componentFrom(MemberInjectionPoint.injectionPointWith(ServiceComponentFieldLevelAnnotation.class.getDeclaredField(FIELD_NAME))), equalTo("COMMAND_CONTROLLER"));
    }

    @Test
    public void shouldReturnClassLevelComponent() throws NoSuchFieldException {
        assertThat(componentFrom(MemberInjectionPoint.injectionPointWith(ServiceComponentClassLevelAnnotation.class.getDeclaredField(FIELD_NAME))), equalTo("COMMAND_HANDLER"));
    }

    @Test
    public void shouldReturnClassLevelAdaptorComponent() throws NoSuchFieldException {
        assertThat(componentFrom(MemberInjectionPoint.injectionPointWith(AdapterAnnotation.class.getDeclaredField(FIELD_NAME))), equalTo("EVENT_LISTENER"));
    }

    @Test
    public void shouldReturnClassLevelCustomAdaptorComponent() throws NoSuchFieldException {
        assertThat(componentFrom(MemberInjectionPoint.injectionPointWith(CustomAdapterAnnotation.class.getDeclaredField(FIELD_NAME))), equalTo("CUSTOM_ADAPTER"));
    }

    @Test
    public void shouldReturnClassLevelComponentForMethodInjectionPoint() throws NoSuchFieldException {
        assertThat(componentFrom(MemberInjectionPoint.injectionPointWith(ServiceComponentClassLevelAnnotationMethod.class.getDeclaredMethods()[0])), equalTo("QUERY_API"));
    }

    @Test
    public void shouldReturnClassLevelFrameworkComponent() throws NoSuchFieldException {
        assertThat(componentFrom(MemberInjectionPoint.injectionPointWith(FrameworkComponentClassLevelAnnotation.class.getDeclaredField(FIELD_NAME))), equalTo("CUSTOM_NAME_ABC"));
    }

    @Test
    public void shouldReturnFieldLevelFrameworkComponent() throws NoSuchFieldException {
        assertThat(componentFrom(MemberInjectionPoint.injectionPointWith(FrameworkComponentFieldLevelAnnotation.class.getDeclaredField(FIELD_NAME))), equalTo("CUSTOM_NAME_BCD"));
    }

    @Test
    public void shouldReturnClassLevelCustomServiceComponent() throws NoSuchFieldException {
        assertThat(componentFrom(MemberInjectionPoint.injectionPointWith(CustomServiceClassLevelAnnotation.class.getDeclaredMethods()[0])), equalTo("CUSTOM_SERVICE_NAME"));
    }

    @Test
    public void shouldReturnFieldLevelCustomServiceComponent() throws NoSuchFieldException {
        assertThat(componentFrom(MemberInjectionPoint.injectionPointWith(CustomServiceFieldLevelAnnotation.class.getDeclaredField(FIELD_NAME))), equalTo("CUSTOM_SERVICE_NAME"));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionOnMissingComponentAnnotation() throws NoSuchFieldException {
        componentFrom(MemberInjectionPoint.injectionPointWith(NoAnnotation.class.getDeclaredField(FIELD_NAME)));
    }

    public static class ServiceComponentFieldLevelAnnotation {

        @Inject
        @ServiceComponent(COMMAND_CONTROLLER)
        Object field;

    }

    @ServiceComponent(COMMAND_HANDLER)
    public static class ServiceComponentClassLevelAnnotation {

        @Inject
        Object field;

    }

    @ServiceComponent(QUERY_API)
    public static class ServiceComponentClassLevelAnnotationMethod {

        @Inject
        public void test(Object field) {

        }

    }

    @Adapter(EVENT_LISTENER)
    public static class AdapterAnnotation {

        @Inject
        Object field;

    }

    @CustomAdapter("CUSTOM_ADAPTER")
    public static class CustomAdapterAnnotation {

        @Inject
        Object field;

    }

    @FrameworkComponent("CUSTOM_NAME_ABC")
    public static class FrameworkComponentClassLevelAnnotation {

        @Inject
        Object field;

    }


    public static class FrameworkComponentFieldLevelAnnotation {

        @Inject
        @FrameworkComponent("CUSTOM_NAME_BCD")
        Object field;

    }

    @CustomServiceComponent("CUSTOM_SERVICE_NAME")
    public static class CustomServiceClassLevelAnnotation {

        @Inject
        public void test(Object field) {

        }

    }

    public static class CustomServiceFieldLevelAnnotation {

        @Inject
        @CustomServiceComponent("CUSTOM_SERVICE_NAME")
        Object field;
    }

    public static class NoAnnotation {

        @Inject
        Object field;

    }
}