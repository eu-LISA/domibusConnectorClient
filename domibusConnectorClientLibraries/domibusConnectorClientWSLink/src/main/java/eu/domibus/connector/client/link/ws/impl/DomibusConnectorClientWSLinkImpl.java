package eu.domibus.connector.client.link.ws.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import eu.domibus.connector.client.exception.DomibusConnectorBackendWebServiceClientException;
import eu.domibus.connector.client.link.DomibusConnectorClientLink;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.domibus.connector.ws.backend.webservice.EmptyRequestType;
import eu.domibus.connector.ws.backend.webservice.GetMessageByIdRequest;
import eu.domibus.connector.ws.backend.webservice.ListPendingMessageIdsResponse;

public class DomibusConnectorClientWSLinkImpl implements DomibusConnectorClientLink {

	private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorClientWSLinkImpl.class);

	@Autowired
	DomibusConnectorBackendWebService connectorWsClient;

	@Override
	public DomibusConnectorMessagesType requestMessagesFromConnector() throws DomibusConnectorBackendWebServiceClientException{
		try {
			DomibusConnectorMessagesType domibusConnectorMessagesType = connectorWsClient.requestMessages(new EmptyRequestType());
			return domibusConnectorMessagesType;
		} catch (Exception e) {
			LOGGER.error("Exeception while requesting messages from Connector!");
			throw new DomibusConnectorBackendWebServiceClientException("Exeception while requesting messages from Connector!", e);
		}
	}

	@Override
	public DomibsConnectorAcknowledgementType submitMessageToConnector(DomibusConnectorMessageType message) throws DomibusConnectorBackendWebServiceClientException{
		try {
			DomibsConnectorAcknowledgementType domibsConnectorAcknowledgementType = connectorWsClient.submitMessage(message);
			return domibsConnectorAcknowledgementType;
		} catch (Exception e) {
			LOGGER.error("Exeception while submitting message with backendId {} to Connector!", message.getMessageDetails().getBackendMessageId());
			throw new DomibusConnectorBackendWebServiceClientException("Exeception while submitting message to Connector!", e);
		}
	}
	
	@Override
	public List<String> listPendingMessages() throws DomibusConnectorBackendWebServiceClientException{
		try {
			ListPendingMessageIdsResponse pendingMessagesIds = connectorWsClient.listPendingMessageIds(new EmptyRequestType());
			return pendingMessagesIds.getMessageTransportIds();
		} catch (Exception e) {
			LOGGER.error("Exeception while calling webservice method 'listPendingMessageIds' at connector!");
			throw new DomibusConnectorBackendWebServiceClientException("Exeception while calling webservice method 'listPendingMessageIds' at connector!", e);
		}
	}
	
	@Override
	public DomibusConnectorMessageType getMessageById(String messageTransportId) throws DomibusConnectorBackendWebServiceClientException {
		try {
			GetMessageByIdRequest request = new GetMessageByIdRequest();
			request.setMessageTransportId(messageTransportId);
			DomibusConnectorMessageType messageById = connectorWsClient.getMessageById(request);
			if(messageById!=null) {
				return messageById;
			}else
				throw new DomibusConnectorBackendWebServiceClientException("Connector returned null when message with messageTransportId "+messageTransportId+" was requested!");
		} catch (Exception e) {
			LOGGER.error("Exeception while requesting message with messageTransportId {} from connector!", messageTransportId);
			throw new DomibusConnectorBackendWebServiceClientException("Exeception while requesting message with messageTransportId "+messageTransportId+" from connector!", e);
		}
	}
	
	@Override
	public void acknowledgeMessage(DomibusConnectorMessageResponseType result) throws DomibusConnectorBackendWebServiceClientException {
		try {
			connectorWsClient.acknowledgeMessage(result);
		} catch (Exception e) {
			LOGGER.error("Exeception while send acknowledge of message with messageTransportId {} to connector!", result.getResponseForMessageId());
			throw new DomibusConnectorBackendWebServiceClientException("Exeception while send acknowledge of message with messageTransportId "+result.getResponseForMessageId()+" to connector!", e);
		}
	}
	
}
