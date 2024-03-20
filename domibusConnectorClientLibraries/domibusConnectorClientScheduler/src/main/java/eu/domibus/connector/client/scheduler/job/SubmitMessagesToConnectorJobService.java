package eu.domibus.connector.client.scheduler.job;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.DomibusConnectorClient;
import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

@Component
@Validated
@Valid
public class SubmitMessagesToConnectorJobService {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SubmitMessagesToConnectorJobService.class);
    
    @Autowired
    private DomibusConnectorClientBackend clientBackend;
    
    @Autowired
	private DomibusConnectorClient connectorClient;
    
   
    public void checkClientBackendForNewMessagesAndSubmitThemToConnector() {
        LocalDateTime startTime = LocalDateTime.now();
        LOGGER.debug("SubmitMessagesToConnectorJob started");

        DomibusConnectorMessagesType newMessages = null;
        try {
        	newMessages = clientBackend.checkClientForNewMessagesToSubmit();
		} catch (DomibusConnectorClientBackendException e1) {
			LOGGER.error("Exception occured at clientBackend.checkClientForNewMessagesToSubmit()",e1);
			e1.printStackTrace();
		}
        
        if(newMessages != null && newMessages.getMessages()!=null && !newMessages.getMessages().isEmpty()) {
        	LOGGER.info("Found {} new message on the client's backend side to submit.", newMessages.getMessages().size());
        	for(DomibusConnectorMessageType newMessage : newMessages.getMessages()) {
        		try {
					connectorClient.submitNewMessageToConnector(newMessage);
				} catch (DomibusConnectorClientException e) {
					LOGGER.error("Exception occured submitting new message to domibusConnector!");
					continue;
				}
        	}
        }
        
        LOGGER.debug("SubmitMessagesToConnectorJob finished after [{}]", Duration.between(startTime, LocalDateTime.now()));
    }
}
