package uk.gov.justice.services.eventsourcing.repository.jdbc.eventlog;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.messaging.DefaultJsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.metadataFrom;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.metadataOf;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.metadataWithDefaults;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.metadataWithRandomUUIDAndName;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;

import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.common.util.Clock;
import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.eventsourcing.repository.jdbc.exception.InvalidStreamIdException;
import uk.gov.justice.services.messaging.DefaultJsonEnvelope;
import uk.gov.justice.services.messaging.DefaultJsonObjectEnvelopeConverter;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.Metadata;
import uk.gov.justice.services.test.utils.core.helper.StoppedClock;

import java.io.IOException;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

@RunWith(MockitoJUnitRunner.class)
public class EventLogConverterTest {

    private final static String PAYLOAD_FIELD_NAME = "field";
    private final static String PAYLOAD_FIELD_VALUE = "Value";

    private final static UUID ID = UUID.randomUUID();
    private final static UUID STREAM_ID = UUID.randomUUID();
    private final static Long SEQUENCE_ID = 5L;
    private final static String NAME = "test.event.did-something";
    private final static String METADATA_JSON = "{\"id\": \"" + ID.toString() + "\", " +
            "\"name\": \"" + NAME + "\"" +
            "}";
    private final static String PAYLOAD_JSON = "{\"" + PAYLOAD_FIELD_NAME + "\": \"" + PAYLOAD_FIELD_VALUE + "\"}";

    private EventLogConverter eventLogConverter;
    private final Clock clock = new StoppedClock(new UtcClock().now());

    @Before
    public void setup() {
        eventLogConverter = new EventLogConverter();
        eventLogConverter.stringToJsonObjectConverter = new StringToJsonObjectConverter();
        eventLogConverter.jsonObjectEnvelopeConverter = new DefaultJsonObjectEnvelopeConverter();
    }

    @Test
    public void shouldCreateEventLog() throws Exception {
        JsonEnvelope envelope = envelope()
                .with(metadataOf(ID, NAME)
                        .withStreamId(STREAM_ID)
                        .withVersion(SEQUENCE_ID)
                        .createdAt(clock.now()))
                .withPayloadOf(PAYLOAD_FIELD_VALUE, PAYLOAD_FIELD_NAME)
                .build();
        EventLog eventLog = eventLogConverter.eventLogOf(envelope);

        assertThat(eventLog.getId(), equalTo(ID));
        assertThat(eventLog.getName(), equalTo(NAME));
        assertThat(eventLog.getStreamId(), equalTo(STREAM_ID));
        assertThat(eventLog.getSequenceId(), equalTo(SEQUENCE_ID));
        assertThat(eventLog.getCreatedAt(), is(clock.now()));
        JSONAssert.assertEquals(METADATA_JSON, eventLog.getMetadata(), false);
        JSONAssert.assertEquals(envelope.payloadAsJsonObject().toString(), eventLog.getPayload(), false);
    }

    @Test(expected = InvalidStreamIdException.class)
    public void shouldThrowExceptionOnNullStreamId() throws Exception {
        eventLogConverter.eventLogOf(envelope().with(metadataWithRandomUUIDAndName()).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnMissingCreatedAt() throws Exception {
        eventLogConverter.eventLogOf((envelope().with(metadataWithRandomUUIDAndName().withStreamId(STREAM_ID)).build()));
    }

    @Test
    public void shouldCreateEnvelope() throws Exception {
        JsonEnvelope actualEnvelope = eventLogConverter.envelopeOf(new EventLog(ID, STREAM_ID, SEQUENCE_ID, NAME, METADATA_JSON, PAYLOAD_JSON, new UtcClock().now()));

        assertThat(actualEnvelope.metadata().id(), equalTo(ID));
        assertThat(actualEnvelope.metadata().name(), equalTo(NAME));
        String actualPayload = actualEnvelope.payloadAsJsonObject().toString();
        JSONAssert.assertEquals(PAYLOAD_JSON, actualPayload, false);
    }



}
