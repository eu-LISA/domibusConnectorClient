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
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessage;

/**
 * The DomibusConnectorClientMessageForm class represents a form for displaying and editing
 * information about a DomibusConnectorClientMessage.
 */
public class DomibusConnectorClientMessageForm extends FormLayout {
    private static final long serialVersionUID = 1L;
    private final TextField backendMessageId = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField ebmsMessageId = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField conversationId = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField originalSender = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField finalRecipient = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField service = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField serviceType = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField action = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField fromPartyId = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField fromPartyRole = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField fromPartyType = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField toPartyId = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField toPartyRole = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField toPartyType = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField storageStatus = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField storageInfo = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField lastConfirmationReceived = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField createdString = FormsUtil.getFormattedTextFieldReadOnly();
    private final TextField messageStatus = FormsUtil.getFormattedTextFieldReadOnly();
    private final Binder<DomibusConnectorClientMessage> binder =
        new Binder<>(DomibusConnectorClientMessage.class);

    public DomibusConnectorClientMessageForm() {
        fillForm();
    }

    private void fillForm() {
        binder.bindInstanceFields(this);

        addFormItem(backendMessageId, "Backend Message ID");
        addFormItem(ebmsMessageId, "EBMS Message ID");
        addFormItem(conversationId, "Conversation ID");
        addFormItem(originalSender, "Original Sender");
        addFormItem(finalRecipient, "Final Recipient");
        addFormItem(service, "Service");
        addFormItem(serviceType, "Service Type");
        addFormItem(action, "Action");
        addFormItem(fromPartyId, "From Party ID");
        addFormItem(fromPartyRole, "From Party Role");
        addFormItem(fromPartyType, "From Party Type");
        addFormItem(toPartyId, "To Party ID");
        addFormItem(toPartyRole, "From Party Role");
        addFormItem(toPartyType, "From Party Type");
        addFormItem(storageStatus, "Storage Status");
        addFormItem(storageInfo, "Storage Info");
        addFormItem(lastConfirmationReceived, "Last Confirmation Received");
        addFormItem(createdString, "Message created at");
        addFormItem(messageStatus, "Message status");
    }

    /**
     * Sets the connector client message in the DomibusConnectorClientMessageForm.
     *
     * @param message the DomibusConnectorClientMessage to be set
     * @see DomibusConnectorClientMessageForm
     * @see DomibusConnectorClientMessage
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
