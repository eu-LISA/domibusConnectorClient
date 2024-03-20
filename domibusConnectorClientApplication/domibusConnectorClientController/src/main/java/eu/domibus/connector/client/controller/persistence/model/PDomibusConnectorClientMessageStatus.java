package eu.domibus.connector.client.controller.persistence.model;

public enum PDomibusConnectorClientMessageStatus {
	PREPARED("The message is prepared to be sent to the domibusConnector but has not been sent yet."), 
	SENDING("The message is about to be sent to the domibusConnector, but has not been accepted by the connector yet."), 
	SENT("The message has been sent to the domibusConnector, but no confirmation has been received yet."), 
	RECEIVING("The message is received from the domibusConnector but still in progress."), 
	RECEIVED("The message has been received from the domibusConnector and processed."), 
	CONFIRMATION_TRIGGERED("A confirmation for the message has been triggered by the backend. The trigger has already been submitted to the domibusConnector"), 
	CONFIRMATION_RECEPTION_FAILED("A confirmation for the message was received from the domibusConnector but processing failed."), 
	CONFIRMED("A confirmation for the message was received from the domibusConnector and processed."),
	CONFIRMATION_DELIVERED_BACKEND("A confirmation for the message was received from the domibusConnector and delivered to backend."),
	FAILED("The processing of the message failed."), 
	REJECTED("The message has been rejected by e-Codex."), 
	ACCEPTED("The message has been accepted by e-Codex."), 
	DELIVERED_BACKEND("A received message has been delivered to the backend successfully"), 
	DELIVERY_FAILED("The delivery of a received message to the backend failed");
	
	private final String statusDescription;
	
	PDomibusConnectorClientMessageStatus(String description){
		this.statusDescription=description;
	}

	public String getStatusDescription() {
		return statusDescription;
	}
	
	
}
