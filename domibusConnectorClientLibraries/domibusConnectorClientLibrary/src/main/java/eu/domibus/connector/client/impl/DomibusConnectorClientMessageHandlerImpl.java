package eu.domibus.connector.client.impl;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

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

@Component
@ConfigurationProperties(prefix = ConnectorClientAutoConfiguration.PREFIX)
@PropertySource("classpath:/connector-client-library-default.properties")
@Validated
@Valid
public class DomibusConnectorClientMessageHandlerImpl implements DomibusConnectorClientMessageHandler {

	private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorClientMessageHandlerImpl.class);

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
	public void prepareInboundMessage(DomibusConnectorMessageType message) throws DCCMessageValidationException, DCCContentMappingException {
		validateInternational(message);

		if(contentMapper!=null) {
			try {
				contentMapper.mapInboundBusinessContent(message);
			} catch (DomibusConnectorClientContentMapperException e) {
				throw new DCCContentMappingException("Exception while mapping inbound message with ebmsId: "+ message.getMessageDetails().getEbmsMessageId(),e);
			}
		}else {
			LOGGER.debug("No instance of DomibusConnectorClientContentMapper found in context!");
		}

		validateLocal(message);
	}

	@Override
	public void prepareOutboundMessage(DomibusConnectorMessageType message) throws DCCMessageValidationException, DCCContentMappingException {
		validateLocal(message);

		if(contentMapper!=null) {
			try {
				contentMapper.mapOutboundBusinessContent(message);
			} catch (DomibusConnectorClientContentMapperException e) {
				throw new DCCContentMappingException("Exception while mapping outbound message!",e);
			}
		}else {
			LOGGER.debug("No instance of DomibusConnectorClientContentMapper found in context!");
		}

		validateInternational(message);

	}

	private void validateInternational(DomibusConnectorMessageType message) throws DCCMessageValidationException {
		if(internationalSchemaValidator!=null) {
			LOGGER.debug("Instance of DCCInternationalSchemaValidator found in context!");
			ValidationResult result = internationalSchemaValidator.validateBusinessContentXML(message);
			result.printValidationResults(LOGGER);
			try {
				checkSchemaValidationResult(result);
			} catch (DCCSchemaValidationException e) {
				throw new DCCMessageValidationException("International Schema Validation has results of max severity level "+schemaValidationMaxSeverityLevel.name()+" or higher! ",e);

			}
		}else {
			LOGGER.debug("No instance of DCCInternationalSchemaValidator found in context!");
		}
	}

	private void validateLocal(DomibusConnectorMessageType message) throws DCCMessageValidationException {
		if(localSchemaValidator!=null) {
			LOGGER.debug("Instance of DCCLocalSchemaValidator found in context!");
			ValidationResult result = localSchemaValidator.validateBusinessContentXML(message);
			result.printValidationResults(LOGGER);
			try {
				checkSchemaValidationResult(result);
			} catch (DCCSchemaValidationException e) {
				throw new DCCMessageValidationException("Local Schema Validation has results of max severity level "+schemaValidationMaxSeverityLevel.name()+" or higher! ",e);

			}
		}else {
			LOGGER.debug("No instance of DCCLocalSchemaValidator found in context!");
		}
	}

	private void checkSchemaValidationResult(ValidationResult result) throws DCCSchemaValidationException {
		LOGGER.debug("Checking schema validation results against maxSeverityLevel {}", schemaValidationMaxSeverityLevel);
		if(schemaValidationMaxSeverityLevel!=null && !result.isOkay()) {
			switch(schemaValidationMaxSeverityLevel) {
			case FATAL_ERROR: 
				if(result.isFatal())
					throw new DCCSchemaValidationException("ValidationResult contains results of severity level "+SeverityLevel.FATAL_ERROR.name()+" or higher!") ;
			case ERROR:
				if(result.isFatal()||result.isError())
					throw new DCCSchemaValidationException("ValidationResult contains results of severity level "+SeverityLevel.ERROR.name()+" or higher!");
			case WARNING:
				if(result.isFatal()||result.isError()||result.isWarning())
					throw new DCCSchemaValidationException("ValidationResult contains results of severity level "+SeverityLevel.WARNING.name()+" or higher!");
			}
		}
	}


	public SeverityLevel getSchemaValidationMaxSeverityLevel() {
		return schemaValidationMaxSeverityLevel;
	}


	public void setSchemaValidationMaxSeverityLevel(SeverityLevel schemaValidationMaxSeverityLevel) {
		this.schemaValidationMaxSeverityLevel = schemaValidationMaxSeverityLevel;
	}



}
