package uk.gov.justice.services.example.cakeshop.it.util;


import uk.gov.justice.services.eventsourcing.repository.jdbc.eventlog.EventLog;
import uk.gov.justice.services.eventsourcing.repository.jdbc.eventlog.EventLogJdbcRepository;
import uk.gov.justice.services.jdbc.persistence.JdbcRepositoryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import javax.sql.DataSource;

/**
 * Standalone repository class to access event streams. To be used in integration testing
 */
public class StandaloneEventLogJdbcRepository extends EventLogJdbcRepository {
    static final String SQL_FIND_ALL = "SELECT * FROM event_log";

    private final DataSource datasource;

    public StandaloneEventLogJdbcRepository(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    protected DataSource getDataSource() {
        return datasource;
    }

}
