package eu.domibus.connector.client.ui.form;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;

public class DynamicMessageForm extends FormLayout{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TextField backendMessageId = FormsUtil.getFormattedTextField();
	private TextField ebmsMessageId = FormsUtil.getFormattedTextField();
	private TextField conversationId = FormsUtil.getFormattedTextField();
	private TextField originalSender = FormsUtil.getFormattedRequiredTextField();
	private TextField finalRecipient = FormsUtil.getFormattedRequiredTextField();
	private TextField service = FormsUtil.getFormattedRequiredTextField();
	private TextField serviceType = FormsUtil.getFormattedTextField();
	private  final TextField action = FormsUtil.getFormattedRequiredTextField();
	private TextField fromPartyId = FormsUtil.getFormattedRequiredTextField();
	private TextField fromPartyRole = FormsUtil.getFormattedTextField();
	private TextField fromPartyType = FormsUtil.getFormattedTextField();
	private TextField toPartyId = FormsUtil.getFormattedRequiredTextField();
	private TextField toPartyRole = FormsUtil.getFormattedTextField();
	private TextField toPartyType = FormsUtil.getFormattedTextField();
	private TextField storageStatus = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField storageInfo = FormsUtil.getFormattedTextFieldReadOnly();
	

	private Binder<DomibusConnectorClientMessage> binder = new Binder<>(DomibusConnectorClientMessage.class);
	

	public Binder<DomibusConnectorClientMessage> getBinder() {
		return binder;
	}
	
	public void setBinder(Binder<DomibusConnectorClientMessage> binder) {
		this.binder = binder;
	}
	
	public TextField getAction() {
		return action;
	}
	
	public DynamicMessageForm() {
			fillForm();
//			setResponsiveSteps(new ResponsiveStep("500px", 2, LabelsPosition.TOP));
		}
		
		private void fillForm() {
			binder.bindInstanceFields(this);
			
			binder.forField(backendMessageId)
			.bind(DomibusConnectorClientMessage::getBackendMessageId, DomibusConnectorClientMessage::setBackendMessageId);
			addFormItem(backendMessageId, "Backend Message ID"); 
			
			binder.forField(ebmsMessageId).withNullRepresentation("")
			.bind(DomibusConnectorClientMessage::getEbmsMessageId, DomibusConnectorClientMessage::setEbmsMessageId);
			addFormItem(ebmsMessageId, "EBMS Message ID"); 
			
			binder.forField(conversationId)
			.bind(DomibusConnectorClientMessage::getConversationId, DomibusConnectorClientMessage::setConversationId);
			addFormItem(conversationId, "Conversation ID"); 
			
			binder.forField(originalSender).withValidator((Validator<String>) (value, context) -> {
				if (value.length() < 1) {
	                return ValidationResult
	                        .error("Original Sender must not be empty!");
	            }
				return ValidationResult.ok();
			}).bind(DomibusConnectorClientMessage::getOriginalSender, DomibusConnectorClientMessage::setOriginalSender);
			addFormItem(originalSender, "Original Sender"); 
			
			binder.forField(finalRecipient).withValidator((Validator<String>) (value, context) -> {
				if (value.length() < 1) {
	                return ValidationResult
	                        .error("Final Recipient must not be empty!");
	            }
				return ValidationResult.ok();
			}).bind(DomibusConnectorClientMessage::getFinalRecipient, DomibusConnectorClientMessage::setFinalRecipient);
			addFormItem(finalRecipient, "Final Recipient"); 
			
			binder.forField(service).withValidator((Validator<String>) (value, context) -> {
				if (value.length() < 1) {
	                return ValidationResult
	                        .error("Service must not be empty!");
	            }
				return ValidationResult.ok();
			}).bind(DomibusConnectorClientMessage::getService, DomibusConnectorClientMessage::setService);
			addFormItem(service, "Service");

			binder.forField(serviceType)
					.bind(DomibusConnectorClientMessage::getServiceType, DomibusConnectorClientMessage::setServiceType);
			addFormItem(serviceType, "Service Type");

			binder.forField(action).withValidator((Validator<String>) (value, context) -> {
				if (value.length() < 1) {
	                return ValidationResult
	                        .error("Action must not be empty!");
	            }
				return ValidationResult.ok();
			}).bind(DomibusConnectorClientMessage::getAction, DomibusConnectorClientMessage::setAction);
			addFormItem(action, "Action"); 
			
			binder.forField(fromPartyId).withValidator((Validator<String>) (value, context) -> {
				if (value.length() < 1) {
	                return ValidationResult
	                        .error("From Party ID must not be empty!");
	            }
				return ValidationResult.ok();
			}).bind(DomibusConnectorClientMessage::getFromPartyId, DomibusConnectorClientMessage::setFromPartyId);
			addFormItem(fromPartyId, "From Party ID"); 
			
			binder.forField(fromPartyRole)
//			.withValidator((Validator<String>) (value, context) -> {
//				if (value.length() < 1) {
//	                return ValidationResult
//	                        .error("From Party Role must not be empty!");
//	            }
//				return ValidationResult.ok();
//			})
			.bind(DomibusConnectorClientMessage::getFromPartyRole, DomibusConnectorClientMessage::setFromPartyRole);
			addFormItem(fromPartyRole, "From Party Role"); 
			
			binder.forField(fromPartyType)
			.bind(DomibusConnectorClientMessage::getFromPartyType, DomibusConnectorClientMessage::setFromPartyType);
			addFormItem(fromPartyType, "From Party Type"); 
			
			binder.forField(toPartyId).withValidator((Validator<String>) (value, context) -> {
				if (value.length() < 1) {
	                return ValidationResult
	                        .error("To Party ID must not be empty!");
	            }
				return ValidationResult.ok();
			}).bind(DomibusConnectorClientMessage::getToPartyId, DomibusConnectorClientMessage::setToPartyId);
			addFormItem(toPartyId, "To Party ID"); 
			
			binder.forField(toPartyRole)
//			.withValidator((Validator<String>) (value, context) -> {
//				if (value.length() < 1) {
//	                return ValidationResult
//	                        .error("To Party Role must not be empty!");
//	            }
//				return ValidationResult.ok();
//			})
			.bind(DomibusConnectorClientMessage::getToPartyRole, DomibusConnectorClientMessage::setToPartyRole);
			addFormItem(toPartyRole, "To Party Role"); 
			
			binder.forField(toPartyType)
			.bind(DomibusConnectorClientMessage::getToPartyType, DomibusConnectorClientMessage::setToPartyType);
			addFormItem(toPartyType, "To Party Type");
			
			addFormItem(storageStatus, "Storage Status");
			addFormItem(storageInfo, "Storage Info");
		
		}
		
		public void setConnectorClientMessage(DomibusConnectorClientMessage message) {
			this.removeAll();
			fillForm();
			binder.setBean(message);
		}
		
		public DomibusConnectorClientMessage getConnectorClientMessage() {
			return binder.getBean();
		}
}
