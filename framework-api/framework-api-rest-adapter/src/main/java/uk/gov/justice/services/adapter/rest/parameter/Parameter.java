package uk.gov.justice.services.adapter.rest.parameter;

import uk.gov.justice.services.rest.ParameterType;

import java.math.BigDecimal;

public interface Parameter {

    ParameterType getType();

    String getName();

    String getStringValue();

    BigDecimal getNumericValue();

    Boolean getBooleanValue();
}