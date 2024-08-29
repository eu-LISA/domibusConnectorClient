/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.impl;

import eu.domibus.connector.client.DomibusConnectorClientMessageHandler;
import eu.domibus.connector.client.exception.DCCContentMappingException;
import eu.domibus.connector.client.exception.DCCMessageValidationException;
import eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper;
import eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapperException;
import eu.domibus.connector.client.schema.validation.DCCInternationalSchemaValidator;
import eu.domibus.connector.client.schema.validation.DCCLocalSchemaValidator;
import eu.domibus.connector.client.schema.validation.DCCSchemaValidationException;
import eu.domibus.connector.client.schema.validation.SeverityLevel;
import eu.domibus.connector.client.schema.validation.ValidationResult;
import eu.domibus.connector.client.spring.ConnectorClientAutoConfiguration;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import javax.validation.Valid;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * The DomibusConnectorClientMessageHandlerImpl class is an implementation of the
 * DomibusConnectorClientMessageHandler interface. It provides methods to prepare a message's
 * business content XML before submitting or delivering it.
 */
@Component
@ConfigurationProperties(prefix = ConnectorClientAutoConfiguration.PREFIX)
@PropertySource("classpath:/connector-client-library-default.properties")
@Validated
@Valid
@Data
public class DomibusConnectorClientMessageHandlerImpl
    implements DomibusConnectorClientMessageHandler {
    private static final Logger LOGGER =
        LogManager.getLogger(DomibusConnectorClientMessageHandlerImpl.class);
    public static final String VALIDATION_RESULT_CONTAINS_RESULTS_OF_SEVERITY_LEVEL =
        "ValidationResult contains results of severity level ";
    public static final String OR_HIGHER = " or higher!";
    @Autowired
    @Nullable
    private DomibusConnectorClientContentMapper contentMapper;
    @Autowired
    @Nullable
    private DCCInternationalSchemaValidator internationalSchemaValidator;
    @Autowired
    @Nullable
    private DCCLocalSchemaValidator localSchemaValidator;
    @Autowired
    @Nullable
    private SeverityLevel schemaValidationMaxSeverityLevel;

    @Override
    public void prepareInboundMessage(DomibusConnectorMessageType message)
        throws DCCMessageValidationException, DCCContentMappingException {
        validateInternational(message);

        if (contentMapper != null) {
            try {
                contentMapper.mapInboundBusinessContent(message);
            } catch (DomibusConnectorClientContentMapperException e) {
                throw new DCCContentMappingException(
                    "Exception while mapping inbound message with ebmsId: "
                        + message.getMessageDetails().getEbmsMessageId(), e);
            }
        } else {
            LOGGER.debug("No instance of DomibusConnectorClientContentMapper found in context!");
        }

        validateLocal(message);
    }

    @Override
    public void prepareOutboundMessage(DomibusConnectorMessageType message)
        throws DCCMessageValidationException, DCCContentMappingException {
        validateLocal(message);

        if (contentMapper != null) {
            try {
                contentMapper.mapOutboundBusinessContent(message);
            } catch (DomibusConnectorClientContentMapperException e) {
                throw new DCCContentMappingException(
                    "Exception while mapping outbound message!",
                    e
                );
            }
        } else {
            LOGGER.debug("No instance of DomibusConnectorClientContentMapper found in context!");
        }

        validateInternational(message);
    }

    private void validateInternational(DomibusConnectorMessageType message)
        throws DCCMessageValidationException {
        if (internationalSchemaValidator != null) {
            LOGGER.debug("Instance of DCCInternationalSchemaValidator found in context!");
            ValidationResult result =
                internationalSchemaValidator.validateBusinessContentXML(message);
            result.printValidationResults(LOGGER);
            try {
                checkSchemaValidationResult(result);
            } catch (DCCSchemaValidationException e) {
                throw new DCCMessageValidationException(
                    "International Schema Validation has results of max severity level "
                        + schemaValidationMaxSeverityLevel.name() + " or higher! ", e);
            }
        } else {
            LOGGER.debug("No instance of DCCInternationalSchemaValidator found in context!");
        }
    }

    private void validateLocal(DomibusConnectorMessageType message)
        throws DCCMessageValidationException {
        if (localSchemaValidator != null) {
            LOGGER.debug("Instance of DCCLocalSchemaValidator found in context!");
            ValidationResult result = localSchemaValidator.validateBusinessContentXML(message);
            result.printValidationResults(LOGGER);
            try {
                checkSchemaValidationResult(result);
            } catch (DCCSchemaValidationException e) {
                throw new DCCMessageValidationException(
                    "Local Schema Validation has results of max severity level "
                        + schemaValidationMaxSeverityLevel.name() + " or higher! ", e);
            }
        } else {
            LOGGER.debug("No instance of DCCLocalSchemaValidator found in context!");
        }
    }

    private void checkSchemaValidationResult(ValidationResult result)
        throws DCCSchemaValidationException {
        LOGGER.debug(
            "Checking schema validation results against maxSeverityLevel {}",
            schemaValidationMaxSeverityLevel
        );
        if (schemaValidationMaxSeverityLevel != null && !result.isOkay()) {
            switch (schemaValidationMaxSeverityLevel) {
                case FATAL_ERROR:
                    if (result.isFatal()) {
                        throw new DCCSchemaValidationException(
                            VALIDATION_RESULT_CONTAINS_RESULTS_OF_SEVERITY_LEVEL
                                + SeverityLevel.FATAL_ERROR.name() + OR_HIGHER);
                    }
                    break;
                case ERROR:
                    if (result.isFatal() || result.isError()) {
                        throw new DCCSchemaValidationException(
                            VALIDATION_RESULT_CONTAINS_RESULTS_OF_SEVERITY_LEVEL
                                + SeverityLevel.ERROR.name() + OR_HIGHER);
                    }
                    break;
                case WARNING:
                    if (result.isFatal() || result.isError() || result.isWarning()) {
                        throw new DCCSchemaValidationException(
                            VALIDATION_RESULT_CONTAINS_RESULTS_OF_SEVERITY_LEVEL
                                + SeverityLevel.WARNING.name() + OR_HIGHER);
                    }
                    break;
                default:
            }
        }
    }
}
