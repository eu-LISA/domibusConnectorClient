package eu.domibus.connector.client.link.ws.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import eu.domibus.connector.client.DomibusConnectorDeliveryClient;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;

public class DomibusConnectorClientDeliveryWsImpl implements DomibusConnectorBackendDeliveryWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorClientDeliveryWsImpl.class);
    
    @Autowired
    private DomibusConnectorDeliveryClient deliveryClient;

    @Override
    public DomibsConnectorAcknowledgementType deliverMessage(DomibusConnectorMessageType msg) {
        
    	LOGGER.info("#deliverMessage: called");
    	DomibsConnectorAcknowledgementType ackResponse = new DomibsConnectorAcknowledgementType();

        if (msg.getMessageContent() != null) {
        	LOGGER.debug("#deliverMessage: received message is a business message...");
        	 try {
             	deliveryClient.receiveDeliveredMessageFromConnector(msg);
             	ackResponse.setResultMessage("Message successfully delivered to client.");
                 ackResponse.setResult(true);
             } catch (Exception e) {
                 LOGGER.error("Exception occured while delivering message from connector to client!", e);
                 ackResponse.setResultMessage(e.getMessage());
                 ackResponse.setResult(false);
             }
		} else if (!CollectionUtils.isEmpty(msg.getMessageConfirmations())) {
			// as there is no message content, but at least one message confirmation,
			// it is a confirmation message
			LOGGER.debug("#deliverMessage: received message is a confirmation message...");
			try {
             	deliveryClient.receiveDeliveredConfirmationMessageFromConnector(msg);
             	ackResponse.setResultMessage("Confirmation message successfully delivered to client.");
                 ackResponse.setResult(true);
             } catch (Exception e) {
                 LOGGER.error("Exception occured while delivering confirmation message from connector to client!", e);
                 ackResponse.setResultMessage(e.getMessage());
                 ackResponse.setResult(false);
             }
		}
       
        return ackResponse;
    }
}
