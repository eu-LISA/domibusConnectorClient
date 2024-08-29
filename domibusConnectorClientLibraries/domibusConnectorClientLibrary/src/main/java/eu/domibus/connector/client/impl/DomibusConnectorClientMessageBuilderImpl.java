/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.impl;

import eu.domibus.connector.client.DomibusConnectorClientMessageBuilder;
import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;
import java.io.ByteArrayInputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * The DomibusConnectorClientMessageBuilderImpl class is an implementation of the
 * DomibusConnectorClientMessageBuilder interface. It provides methods for building
 * DomibusConnectorMessageType messages.
 *
 * @see DomibusConnectorClientMessageBuilder
 */
@Component
public class DomibusConnectorClientMessageBuilderImpl
    implements DomibusConnectorClientMessageBuilder {
    @Override
    public DomibusConnectorMessageType createNewMessage(
        String backendMessageId,
        String ebmsMessageId,
        String conversationId, String businessService,
        String businessServiceType, String businessAction, String fromPartyId,
        String fromPartyIdType,
        String fromPartyRole, String toPartyId, String toPartyIdType, String toPartyRole,
        String finalRecipient,
        String originalSender) {
        var msgDetails = new DomibusConnectorMessageDetailsType();
        msgDetails.setBackendMessageId(backendMessageId);
        msgDetails.setEbmsMessageId(ebmsMessageId);
        msgDetails.setConversationId(conversationId);

        if (StringUtils.hasText(businessService)) {
            var service = new DomibusConnectorServiceType();
            service.setService(businessService);
            service.setServiceType(businessServiceType);
            msgDetails.setService(service);
        }

        if (StringUtils.hasText(businessAction)) {
            var action = new DomibusConnectorActionType();
            action.setAction(businessAction);
            msgDetails.setAction(action);
        }

        var fromParty = new DomibusConnectorPartyType();
        fromParty.setPartyId(fromPartyId);
        fromParty.setPartyIdType(fromPartyIdType);
        fromParty.setRole(fromPartyRole);
        msgDetails.setFromParty(fromParty);

        var toParty = new DomibusConnectorPartyType();
        toParty.setPartyId(toPartyId);
        toParty.setPartyIdType(toPartyIdType);
        toParty.setRole(toPartyRole);
        msgDetails.setToParty(toParty);

        msgDetails.setFinalRecipient(finalRecipient);
        msgDetails.setOriginalSender(originalSender);

        var newMessage = new DomibusConnectorMessageType();
        newMessage.setMessageDetails(msgDetails);

        return newMessage;
    }

    @Override
    public DomibusConnectorMessageType createNewConfirmationMessage(
        String ebmsMessageId,
        String conversationId, DomibusConnectorServiceType businessService,
        String businessAction, DomibusConnectorPartyType fromParty,
        DomibusConnectorPartyType toParty,
        String finalRecipient,
        String originalSender,
        DomibusConnectorConfirmationType confirmationType) {
        DomibusConnectorMessageType newMessage = createNewMessage(
            null,
            null,
            conversationId,
            null,
            null,
            businessAction,
            null,
            null,
            null,
            null,
            null,
            null,
            finalRecipient,
            originalSender
        );

        newMessage.getMessageDetails().setRefToMessageId(ebmsMessageId);
        newMessage.getMessageDetails().setService(businessService);
        newMessage.getMessageDetails().setFromParty(fromParty);
        newMessage.getMessageDetails().setToParty(toParty);

        var confirmation = new DomibusConnectorMessageConfirmationType();
        confirmation.setConfirmationType(confirmationType);

        newMessage.getMessageConfirmations().add(confirmation);

        return newMessage;
    }

    @Override
    public DomibusConnectorMessageType addBusinessContentXMLAsBinary(
        DomibusConnectorMessageType message,
        byte[] businessContent) {
        var streamSource = new StreamSource(new ByteArrayInputStream(businessContent));

        return addBusinessContentXMLAsStream(message, streamSource);
    }

    @Override
    public DomibusConnectorMessageType addBusinessContentXMLAsStream(
        DomibusConnectorMessageType message,
        Source businessContent) {

        if (message.getMessageContent() == null) {
            message.setMessageContent(new DomibusConnectorMessageContentType());
        }

        message.getMessageContent().setXmlContent(businessContent);

        return message;
    }

    @Override
    public DomibusConnectorMessageType addBusinessDocumentAsBinary(
        DomibusConnectorMessageType message,
        byte[] businessDocument, String businessDocumentName) {

        var businessDocumentDh = createDataHandler(businessDocument, "application/octet-stream");

        return addBusinessDocumentAsStream(message, businessDocumentDh, businessDocumentName);
    }

    @Override
    public DomibusConnectorMessageType addBusinessDocumentAsStream(
        DomibusConnectorMessageType message,
        DataHandler businessDocument, String businessDocumentName) {

        if (message.getMessageContent() == null) {
            message.setMessageContent(new DomibusConnectorMessageContentType());
        }

        var document = new DomibusConnectorMessageDocumentType();

        document.setDocumentName(businessDocumentName);
        document.setDocument(businessDocument);

        message.getMessageContent().setDocument(document);

        return message;
    }

    @Override
    public DomibusConnectorMessageType addDetachedSignatureForBusinessDocument(
        DomibusConnectorMessageType message,
        byte[] detachedSignature, String detachedSignatureName,
        DomibusConnectorDetachedSignatureMimeType detachedSignatureType) {
        if (message != null && message.getMessageContent() != null
            && message.getMessageContent().getDocument() != null) {

            var signatureType = new DomibusConnectorDetachedSignatureType();

            signatureType.setDetachedSignature(detachedSignature);
            signatureType.setDetachedSignatureName(detachedSignatureName);
            signatureType.setMimeType(detachedSignatureType);

            message.getMessageContent().getDocument().setDetachedSignature(signatureType);
            return message;
        }
        return null;
    }

    @Override
    public DomibusConnectorMessageType addBusinessAttachmentAsBinaryToMessage(
        DomibusConnectorMessageType message,
        String businessAttachmentIdentifier, byte[] businessAttachment,
        String businessAttachmentName,
        String businessAttachmentMimeType, String businessAttachmentDescription) {

        var businessAttachmentDh =
            createDataHandler(businessAttachment, "application/octet-stream");

        return addBusinessAttachmentAsStreamToMessage(
            message, businessAttachmentIdentifier, businessAttachmentDh, businessAttachmentName,
            businessAttachmentMimeType, businessAttachmentDescription
        );
    }

    @Override
    public DomibusConnectorMessageType addBusinessAttachmentAsStreamToMessage(
        DomibusConnectorMessageType message,
        String businessAttachmentIdentifier, DataHandler businessAttachment,
        String businessAttachmentName,
        String businessAttachmentMimeType, String businessAttachmentDescription) {

        var attachment = new DomibusConnectorMessageAttachmentType();
        attachment.setAttachment(businessAttachment);
        attachment.setDescription(businessAttachmentDescription);
        attachment.setIdentifier(businessAttachmentIdentifier);
        attachment.setMimeType(businessAttachmentMimeType);
        attachment.setName(businessAttachmentName);

        message.getMessageAttachments().add(attachment);

        return message;
    }

    private DataHandler createDataHandler(byte[] content, String type) {
        DataSource ds = new ByteArrayDataSource(content, type);

        return new DataHandler(ds);
    }
}
