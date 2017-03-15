package uk.gov.justice.services.core.interceptor;

import static uk.gov.justice.services.core.interceptor.DefaultContextPayload.contextPayloadWith;
import static uk.gov.justice.services.core.interceptor.DefaultContextPayload.contextPayloadWithNoEnvelope;
import static uk.gov.justice.services.core.interceptor.DefaultContextPayload.copyWithEnvelope;

import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.Optional;

public class InterceptorContext {

    private final ContextPayload input;
    private final ContextPayload output;

    /**
     * Construct an InterceptorContext that contains an input {@link ContextPayload}, an output
     * {@link ContextPayload}, and an injection point of interceptor chain process.
     *
     * @param input  the input ContextPayload
     * @param output the output ContextPayload
     */
    private InterceptorContext(final ContextPayload input, final ContextPayload output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Create an interceptor context with an input {@link ContextPayload} that wraps the input
     * envelope, an output {@link ContextPayload} with no envelope and an injection point.
     *
     * @param input the input JsonEnvelope
     * @return the new InterceptorContext
     */
    public static InterceptorContext interceptorContextWithInput(final JsonEnvelope input) {
        return new InterceptorContext(contextPayloadWith(input), contextPayloadWithNoEnvelope());
    }

    /**
     * Create a copy of the provided interceptor context that will contain the provided input
     * envelope.
     *
     * @param interceptorContext the interceptor context to copy
     * @param inputEnvelope      the inputEnvelope JsonEnvelope to set as input
     * @return the new InterceptorContext
     */
    public static InterceptorContext copyWithInput(final InterceptorContext interceptorContext, final JsonEnvelope inputEnvelope) {
        return new InterceptorContext(
                copyWithEnvelope(interceptorContext.inputContext(), inputEnvelope),
                interceptorContext.outputContext());
    }

    /**
     * Create a copy of the provided interceptor context that will contain the provided output
     * envelope.
     *
     * @param interceptorContext the interceptor context to copy
     * @param outputEnvelope     the outputEnvelope JsonEnvelope to set as the output
     * @return the new InterceptorContext
     */
    public static InterceptorContext copyWithOutput(final InterceptorContext interceptorContext, final JsonEnvelope outputEnvelope) {
        return new InterceptorContext(
                interceptorContext.inputContext(),
                copyWithEnvelope(interceptorContext.outputContext(), outputEnvelope));
    }

    public JsonEnvelope inputEnvelope() {
        return input.getEnvelope().orElseThrow(() -> new IllegalStateException("No input envelope set."));
    }

    public Optional<JsonEnvelope> outputEnvelope() {
        return output.getEnvelope();
    }

    public Optional<Object> getInputParameter(final String name) {
        return input.getParameter(name);
    }

    public void setInputParameter(final String name, final Object parameter) {
        input.setParameter(name, parameter);
    }

    public Optional<Object> getOutputParameter(final String name) {
        return output.getParameter(name);
    }

    public void setOutputParameter(final String name, final Object parameter) {
        output.setParameter(name, parameter);
    }

    private ContextPayload inputContext() {
        return input;
    }

    private ContextPayload outputContext() {
        return output;
    }
}
