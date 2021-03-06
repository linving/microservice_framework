package uk.gov.justice.services.core.annotation;

import uk.gov.justice.services.core.dispatcher.DispatcherCache;
import uk.gov.justice.services.core.extension.AnnotationScanner;

import javax.enterprise.inject.spi.InjectionPoint;

public enum ServiceComponentLocation {

    LOCAL,
    REMOTE;

    /**
     * Used by the {@link AnnotationScanner} to get the location for a given Class.  Checks for the
     * {@link Remote} annotation and returns REMOTE if present on the given Class.
     *
     * @param clazz the Class to check
     * @return the service component location
     */
    public static ServiceComponentLocation componentLocationFrom(final Class<?> clazz) {
        return clazz.isAnnotationPresent(Remote.class) ? REMOTE : LOCAL;
    }

    /**
     * Used by the {@link DispatcherCache} to get the location for a given InjectionPoint.  Checks
     * for {@link Adapter} or {@link CustomAdapter} annotations and returns LOCAL if present on the
     * Class containing the given InjectionPoint.
     *
     * @param injectionPoint the InjectionPoint to check
     * @return the service component location
     */
    public static ServiceComponentLocation componentLocationFrom(final InjectionPoint injectionPoint) {
        final Class<?> targetClass = injectionPoint.getMember().getDeclaringClass();
        return targetClass.isAnnotationPresent(Adapter.class)
                || targetClass.isAnnotationPresent(CustomAdapter.class) ? LOCAL : REMOTE;
    }
}
