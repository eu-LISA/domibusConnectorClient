package eu.domibus.connector.client.scheduler.job;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.DomibusConnectorClient;
import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;


@Component
@Validated
@Valid
public class GetMessagesFromConnectorJobService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetMessagesFromConnectorJobService.class);

	@Autowired
	private DomibusConnectorClientBackend clientBackend;

	@Autowired
	private DomibusConnectorClient connectorClient;

	@Autowired
	GetMessagesFromConnectorJobConfigurationProperties properties;


	public void requestNewMessagesFromConnectorAndDeliverThemToClientBackend() throws DomibusConnectorClientException {
		//		DomibusConnectorMessagesType messages = null;
		LocalDateTime startTime = LocalDateTime.now();
		LOGGER.debug("GetMessagesFromConnectorJob started");

		//		messages = connectorClient.requestNewMessagesFromConnector();
		Map<String, DomibusConnectorMessageType> received = connectorClient.requestNewMessagesFromConnector(properties.getMaxFetchCount(), properties.isAutoAcknowledgeMessages());

		if (received!=null && !CollectionUtils.isEmpty(received.values())) {
			LOGGER.info("{} new messages from connector to store...", received.size());
			received.forEach((transportId,message) -> {
				boolean isBusinessMessage = message.getMessageContent()!=null;
				boolean isEvidenceMessage = message.getMessageConfirmations()!=null && !message.getMessageConfirmations().isEmpty();
				if(properties.isAutoAcknowledgeMessages()) {
					if(isBusinessMessage) {
						try {
							clientBackend.deliverNewMessageToClientBackend(message);
						} catch (DomibusConnectorClientBackendException e1) {
							LOGGER.error("Exception occured delivering new message to the client backend", e1);

						}
					}else if (isEvidenceMessage) {
						try {
							clientBackend.deliverNewConfirmationToClientBackend(message);
						} catch (DomibusConnectorClientBackendException e1) {
							LOGGER.error("Exception occured delivering new confirmation to the client backend", e1);

						}
					}
				} else {
					if(isBusinessMessage) {
						try {
							clientBackend.deliverNewAcknowledgeableMessageToClientBackend(message, transportId);
						} catch (DomibusConnectorClientBackendException e1) {
							LOGGER.error("Exception occured delivering new message to the client backend", e1);

						}
					}else if (isEvidenceMessage) {
						try {
							clientBackend.deliverNewAcknowledgeableConfirmationToClientBackend(message, transportId);
						} catch (DomibusConnectorClientBackendException e1) {
							LOGGER.error("Exception occured delivering new confirmation to the client backend", e1);

						}
					}
				}

			});
		}else {
			LOGGER.debug("No new messages from connector to store received.");
		}
		LOGGER.debug("GetMessagesFromConnectorJob finished after [{}]", Duration.between(startTime, LocalDateTime.now()));
	}

}
