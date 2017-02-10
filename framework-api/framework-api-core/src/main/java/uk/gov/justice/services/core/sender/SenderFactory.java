package uk.gov.justice.services.core.sender;

import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.core.annotation.Component;

public interface SenderFactory {
    Sender createSender(final String componentDestination);
}
