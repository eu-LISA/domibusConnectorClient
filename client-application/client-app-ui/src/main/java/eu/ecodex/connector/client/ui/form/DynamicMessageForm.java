/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.ui.form;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessage;
import lombok.Data;

/**
 * A class that represents a dynamic message form.
 */
@Data
public class DynamicMessageForm extends FormLayout {
    private static final long serialVersionUID = 1L;
    private final TextField backendMessageId = FormsUtil.getFormattedTextField();
    private final TextField ebmsMessageId = FormsUtil.getFormattedTextField();
    private final TextField conversationId = FormsUtil.getFormattedTextField();
    private final TextField originalSender = FormsUtil.getFormattedRequiredTextField();
    private final TextField finalRecipient = FormsUtil.getFormattedRequiredTextField();
    private final TextField service = FormsUtil.getFormattedRequiredTextField();
    private final TextField serviceType = FormsUtil.getFormattedTextField();
    private final TextField action = FormsUtil.getFormattedRequiredTextField();
    private final TextField fromPartyId = FormsUtil.getFormattedRequiredTextField();
    private final TextField fromPartyRole = FormsUtil.getFormattedTextField();
    private final TextField fromPartyType = FormsUtil.getFormattedTextField();
    private final TextField toPartyId = FormsUtil.getFormattedRequiredTextField();
    private final TextField toPartyRole = FormsUtil.getFormattedTextField();
    private final TextField toPartyType = FormsUtil.getFormattedTextField();
    private final TextField storageStatus = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField storageInfo = FormsUtil.getFormattedTextFieldReadOnly();
    private Binder<DomibusConnectorClientMessage> binder =
        new Binder<>(DomibusConnectorClientMessage.class);

    public DynamicMessageForm() {
        fillForm();
    }

    private void fillForm() {
        binder.bindInstanceFields(this);
        binder.forField(backendMessageId)
              .bind(
                  DomibusConnectorClientMessage::getBackendMessageId,
                  DomibusConnectorClientMessage::setBackendMessageId
              );
        addFormItem(backendMessageId, "Backend Message ID");

        binder.forField(ebmsMessageId).withNullRepresentation("")
              .bind(
                  DomibusConnectorClientMessage::getEbmsMessageId,
                  DomibusConnectorClientMessage::setEbmsMessageId
              );
        addFormItem(ebmsMessageId, "EBMS Message ID");

        binder.forField(conversationId)
              .bind(
                  DomibusConnectorClientMessage::getConversationId,
                  DomibusConnectorClientMessage::setConversationId
              );
        addFormItem(conversationId, "Conversation ID");

        binder.forField(originalSender).withValidator((Validator<String>) (value, context) -> {
            if (value.isEmpty()) {
                return ValidationResult.error("Original Sender must not be empty!");
            }
            return ValidationResult.ok();
        }).bind(DomibusConnectorClientMessage::getOriginalSender,
                DomibusConnectorClientMessage::setOriginalSender
        );
        addFormItem(originalSender, "Original Sender");

        binder.forField(finalRecipient).withValidator((Validator<String>) (value, context) -> {
            if (value.isEmpty()) {
                return ValidationResult.error("Final Recipient must not be empty!");
            }
            return ValidationResult.ok();
        }).bind(DomibusConnectorClientMessage::getFinalRecipient,
                DomibusConnectorClientMessage::setFinalRecipient
        );
        addFormItem(finalRecipient, "Final Recipient");

        binder.forField(service).withValidator((Validator<String>) (value, context) -> {
            if (value.isEmpty()) {
                return ValidationResult.error("Service must not be empty!");
            }
            return ValidationResult.ok();
        }).bind(DomibusConnectorClientMessage::getService,
                DomibusConnectorClientMessage::setService
        );
        addFormItem(service, "Service");

        binder.forField(serviceType)
              .bind(
                  DomibusConnectorClientMessage::getServiceType,
                  DomibusConnectorClientMessage::setServiceType
              );
        addFormItem(serviceType, "Service Type");

        binder.forField(action).withValidator((Validator<String>) (value, context) -> {
            if (value.isEmpty()) {
                return ValidationResult.error("Action must not be empty!");
            }
            return ValidationResult.ok();
        }).bind(DomibusConnectorClientMessage::getAction, DomibusConnectorClientMessage::setAction);
        addFormItem(action, "Action");

        binder.forField(fromPartyId).withValidator((Validator<String>) (value, context) -> {
            if (value.isEmpty()) {
                return ValidationResult.error("From Party ID must not be empty!");
            }
            return ValidationResult.ok();
        }).bind(DomibusConnectorClientMessage::getFromPartyId,
                DomibusConnectorClientMessage::setFromPartyId
        );
        addFormItem(fromPartyId, "From Party ID");

        binder.forField(fromPartyRole)
              .bind(
                  DomibusConnectorClientMessage::getFromPartyRole,
                  DomibusConnectorClientMessage::setFromPartyRole
              );
        addFormItem(fromPartyRole, "From Party Role");

        binder.forField(fromPartyType)
              .bind(
                  DomibusConnectorClientMessage::getFromPartyType,
                  DomibusConnectorClientMessage::setFromPartyType
              );
        addFormItem(fromPartyType, "From Party Type");

        binder.forField(toPartyId).withValidator((Validator<String>) (value, context) -> {
            if (value.isEmpty()) {
                return ValidationResult.error("To Party ID must not be empty!");
            }
            return ValidationResult.ok();
        }).bind(DomibusConnectorClientMessage::getToPartyId,
                DomibusConnectorClientMessage::setToPartyId
        );
        addFormItem(toPartyId, "To Party ID");

        binder.forField(toPartyRole)
              .bind(
                  DomibusConnectorClientMessage::getToPartyRole,
                  DomibusConnectorClientMessage::setToPartyRole
              );
        addFormItem(toPartyRole, "To Party Role");

        binder.forField(toPartyType)
              .bind(
                  DomibusConnectorClientMessage::getToPartyType,
                  DomibusConnectorClientMessage::setToPartyType
              );
        addFormItem(toPartyType, "To Party Type");

        addFormItem(storageStatus, "Storage Status");
        addFormItem(storageInfo, "Storage Info");
    }

    /**
     * Sets the {@code DomibusConnectorClientMessage} to be displayed in the form.
     * This method updates the form fields based on the values of the message properties.
     *
     * @param message the {@code DomibusConnectorClientMessage} to be set
     */
    public void setConnectorClientMessage(DomibusConnectorClientMessage message) {
        this.removeAll();
        fillForm();
        binder.setBean(message);
    }

    public DomibusConnectorClientMessage getConnectorClientMessage() {
        return binder.getBean();
    }
}
