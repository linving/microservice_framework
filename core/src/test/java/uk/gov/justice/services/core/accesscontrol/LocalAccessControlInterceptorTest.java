package uk.gov.justice.services.core.accesscontrol;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_API;
import static uk.gov.justice.services.core.interceptor.DefaultInterceptorContext.interceptorContextWithInput;

import uk.gov.justice.services.core.annotation.Adapter;
import uk.gov.justice.services.core.interceptor.DefaultInterceptorChain;
import uk.gov.justice.services.core.interceptor.Interceptor;
import uk.gov.justice.services.core.interceptor.InterceptorChain;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessor;
import uk.gov.justice.services.core.interceptor.InterceptorContext;
import uk.gov.justice.services.core.interceptor.Target;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LocalAccessControlInterceptorTest {

    private static final int ACCESS_CONTROL_PRIORITY = 6000;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private JsonEnvelope envelope;

    @Mock
    private AccessControlService accessControlService;

    @Mock
    private AccessControlFailureMessageGenerator accessControlFailureMessageGenerator;

    @InjectMocks
    private LocalAccessControlInterceptor localAccessControlInterceptor;

    private InterceptorChain interceptorChain;

    @Before
    public void setup() throws Exception {
        final Deque<Interceptor> interceptors = new LinkedList<>();
        interceptors.add(localAccessControlInterceptor);

        final Target target = context -> context;

        interceptorChain = new DefaultInterceptorChain(interceptors, target);
    }

    @Test
    public void shouldApplyAccessControlToInputIfLocalComponent() throws Exception {
        final InterceptorContext inputContext = interceptorContextWithInput(envelope);
        when(accessControlService.checkAccessControl(envelope)).thenReturn(Optional.empty());

        interceptorChain.processNext(inputContext);
        verify(accessControlService).checkAccessControl(envelope);
    }

    @Test
    public void shouldReturnAccessControlPriority() throws Exception {
        assertThat(localAccessControlInterceptor.priority(), is(ACCESS_CONTROL_PRIORITY));
    }

    @Test
    public void shouldThrowAccessControlViolationExceptionIfAccessControlFailsForInput() throws Exception {
        final InterceptorContext inputContext = interceptorContextWithInput(envelope);
        final AccessControlViolation accessControlViolation = new AccessControlViolation("reason");

        when(accessControlService.checkAccessControl(envelope)).thenReturn(Optional.of(accessControlViolation));
        when(accessControlFailureMessageGenerator.errorMessageFrom(envelope, accessControlViolation)).thenReturn("Error message");

        exception.expect(AccessControlViolationException.class);
        exception.expectMessage("Error message");

        interceptorChain.processNext(inputContext);
    }

    @Adapter(COMMAND_API)
    public static class TestCommandLocal {
        @Inject
        InterceptorChainProcessor interceptorChainProcessor;

        public void dummyMethod() {

        }
    }

    public static class TestCommandRemote {
        @Inject
        InterceptorChainProcessor interceptorChainProcessor;

        public void dummyMethod() {

        }
    }
}