package eu.domibus.connector.client.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.DomibusConnectorClientMessageHandler;
import eu.domibus.connector.client.DomibusConnectorDeliveryClient;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.link.ws.configuration.ConnectorLinkWSProperties;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
@ConditionalOnProperty(prefix = ConnectorLinkWSProperties.PREFIX, value = ConnectorLinkWSProperties.PUSH_ENABLED_PROPERTY_NAME, matchIfMissing = false)
//@ConditionalOnMissingBean(value = DomibusConnectorDeliveryClient.class)
public class DomibusConnectorDeliveryClientImpl implements DomibusConnectorDeliveryClient {

	private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorDeliveryClientImpl.class);
	
	@Autowired
	private DomibusConnectorClientMessageHandler messageHandler;
	
	@Autowired
    private DomibusConnectorClientBackend clientBackend;
	
	public DomibusConnectorDeliveryClientImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void receiveDeliveredMessageFromConnector(DomibusConnectorMessageType message)
			throws DomibusConnectorClientException {
		LOGGER.info("#receiveDeliveredMessageFromConnector: received new message from connector via push.");
		messageHandler.prepareInboundMessage(message);
		
		try {
			clientBackend.deliverNewMessageToClientBackend(message);
		} catch (DomibusConnectorClientBackendException e) {
			throw new DomibusConnectorClientException(e);
		}
		LOGGER.debug("receiveDeliveredMessageFromConnector: received message delivered to client backend.");
	}

	@Override
	public void receiveDeliveredConfirmationMessageFromConnector(DomibusConnectorMessageType message)
			throws DomibusConnectorClientException {
		LOGGER.info("#receiveDeliveredConfirmationMessageFromConnector: received new confirmation from connector via push.");
		try {
			clientBackend.deliverNewConfirmationToClientBackend(message);
		} catch (DomibusConnectorClientBackendException e) {
			throw new DomibusConnectorClientException(e);
		}
		LOGGER.debug("receiveDeliveredConfirmationMessageFromConnector: received confirmation delivered to client backend.");
	}

}
