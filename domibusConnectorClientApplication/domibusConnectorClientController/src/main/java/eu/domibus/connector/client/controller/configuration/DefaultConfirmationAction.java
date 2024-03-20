package eu.domibus.connector.client.controller.configuration;

public class DefaultConfirmationAction {
	
	private String submissionAcceptanceRejection;
	private String relayREMMDAcceptanceRejection;
	private String deliveryNonDeliveryToRecipient;
	private String retrievalNonRetrievalToRecipient;
	
	public String getSubmissionAcceptanceRejection() {
		return submissionAcceptanceRejection;
	}
	public void setSubmissionAcceptanceRejection(String submissionAcceptanceRejection) {
		this.submissionAcceptanceRejection = submissionAcceptanceRejection;
	}
	public String getRelayREMMDAcceptanceRejection() {
		return relayREMMDAcceptanceRejection;
	}
	public void setRelayREMMDAcceptanceRejection(String relayREMMDAcceptanceRejection) {
		this.relayREMMDAcceptanceRejection = relayREMMDAcceptanceRejection;
	}
	public String getDeliveryNonDeliveryToRecipient() {
		return deliveryNonDeliveryToRecipient;
	}
	public void setDeliveryNonDeliveryToRecipient(String deliveryNonDeliveryToRecipient) {
		this.deliveryNonDeliveryToRecipient = deliveryNonDeliveryToRecipient;
	}
	public String getRetrievalNonRetrievalToRecipient() {
		return retrievalNonRetrievalToRecipient;
	}
	public void setRetrievalNonRetrievalToRecipient(String retrievalNonRetrievalToRecipient) {
		this.retrievalNonRetrievalToRecipient = retrievalNonRetrievalToRecipient;
	}
	

}
