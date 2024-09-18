/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client;

import eu.ecodex.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.ecodex.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.domain.transition.DomibusConnectorPartyType;
import eu.ecodex.connector.domain.transition.DomibusConnectorServiceType;
import jakarta.activation.DataHandler;
import javax.xml.transform.Source;

/**
 * This interface is implemented by default.
 *
 * <p>The implementation offers helper methods to create build the
 * {@link DomibusConnectorMessageType}.
 *
 * @author Bernhard Rieder
 */
public interface DomibusConnectorClientMessageBuilder {
    /**
     * This method creates a new {@link DomibusConnectorMessageType}, adds the
     * {@link DomibusConnectorMessageDetailsType} and fills its attributes.
     *
     * @param backendMessageId    - The messageId as referenced on the backend side
     * @param ebmsMessageId       - The EBMS message id if given by the backend. May be null. Will
     *                            be generated on eCodex level if null! If given by the backend, the
     *                            id must be a worldwide unique one (for example by being generated
     *                            using UUID).
     * @param conversationId      - The conversation id if given by the backend. May be null. Will
     *                            be generated on eCodex level if null! If given by the backend, the
     *                            id must be a worldwide unique one (for example by being generated
     *                            using UUID).
     * @param businessService     - The business service this message is part of. Must be defined by
     *                            the use case (PModes).
     * @param businessServiceType - The business service type this message is part of. Must be
     *                            defined by the use case (PModes).
     * @param businessAction      - The business action this message is part of. Must be defined by
     *                            the use case (PModes).
     * @param fromPartyId         - The ID of the Party this message was sent from. May be null.
     *                            Will be set by the connector, if properly configured.
     * @param fromPartyIdType     - The ID type of the Party this message was sent from. May be
     *                            null. Will be set by the connector, if properly configured.
     * @param fromPartyRole       - The role of the Party this message should was sent from. May be
     *                            null. Will be set by the connector, if properly configured.
     * @param toPartyId           - The ID of the Party this message should be sent to. Must be
     *                            defined by the use case (PModes).
     * @param toPartyIdType       - The ID type of the Party this message should be sent to. Must be
     *                            defined by the use case (PModes).
     * @param toPartyRole         - The role of the Party this message should be sent to. May be
     *                            null. Will be set by the connector, if properly configured.
     * @param finalRecipient      - The electronic identifier of the final recipient of the
     *                            message.
     * @param originalSender      - The electronic identifier of the original sender of the
     *                            message.
     * @return A new message object with a {@link DomibusConnectorMessageDetailsType} added.
     */
    public DomibusConnectorMessageType createNewMessage(
        String backendMessageId,
        String ebmsMessageId,
        String conversationId,
        String businessService,
        String businessServiceType,
        String businessAction,
        String fromPartyId,
        String fromPartyIdType,
        String fromPartyRole,
        String toPartyId,
        String toPartyIdType,
        String toPartyRole,
        String finalRecipient,
        String originalSender);

    /**
     * Adds a business content XML to the {@link DomibusConnectorMessageType}. The message object
     * needs to be instantiated first via the method
     * {@link #createNewMessage(String, String, String, String, String, String, String, String,
     * String, String, String, String, String, String)}.
     *
     * @param message         - the message object
     * @param businessContent - the business content XML as bytes.
     * @return The message object with a {@link DomibusConnectorMessageContentType} added
     */
    public DomibusConnectorMessageType addBusinessContentXMLAsBinary(
        DomibusConnectorMessageType message,
        byte[] businessContent);

    /**
     * Adds a business content XML to the {@link DomibusConnectorMessageType}. The message object
     * needs to be instantiated first via the method
     * {@link #createNewMessage(String, String, String, String, String, String, String, String,
     * String, String, String, String, String, String)}.
     *
     * @param message         - the message object
     * @param businessContent - the business content XML as stream object.
     * @return The message object with a {@link DomibusConnectorMessageContentType} added
     */
    public DomibusConnectorMessageType addBusinessContentXMLAsStream(
        DomibusConnectorMessageType message,
        Source businessContent);

    /**
     * Adds the business document to the {@link DomibusConnectorMessageType}. The message object
     * needs to be instantiated first and a businessContent needs to be added via the method
     * {@link #addBusinessContentXMLAsBinary(DomibusConnectorMessageType, byte[])} or
     * {@link #addBusinessContentXMLAsStream(DomibusConnectorMessageType, Source)}.
     *
     * @param message              - the message object
     * @param businessDocument     - the business document as bytes.
     * @param businessDocumentName - the name of the business document.
     * @return The message object with a {@link DomibusConnectorMessageDocumentType} added to its
     *      {@link DomibusConnectorMessageContentType}.
     */
    public DomibusConnectorMessageType addBusinessDocumentAsBinary(
        DomibusConnectorMessageType message,
        byte[] businessDocument,
        String businessDocumentName);

    /**
     * Adds the business document to the {@link DomibusConnectorMessageType}. The message object
     * needs to be instantiated first and a businessContent needs to be added via the method
     * {@link #addBusinessContentXMLAsBinary(DomibusConnectorMessageType, byte[])} or
     * {@link #addBusinessContentXMLAsStream(DomibusConnectorMessageType, Source)}.
     *
     * @param message              - the message object
     * @param businessDocument     - the business document as stream object.
     * @param businessDocumentName - the name of the business document.
     * @return The message object with a {@link DomibusConnectorMessageDocumentType} added to its
     *      {@link DomibusConnectorMessageContentType}.
     */
    public DomibusConnectorMessageType addBusinessDocumentAsStream(
        DomibusConnectorMessageType message,
        DataHandler businessDocument,
        String businessDocumentName);

    /**
     * Generates and adds a {@link DomibusConnectorDetachedSignatureType} to the
     * {@link DomibusConnectorMessageDocumentType}. A detached signature may be given to validate
     * this signature on the business document. The validation happens in the domibusConnector.
     *
     * @param message               - the message object
     * @param detachedSignature     - the detached signature itself as bytes.
     * @param detachedSignatureName - the name for this detached signature.
     * @param detachedSignatureType - the mime type of the detached signature.
     * @return The message object with a {@link DomibusConnectorDetachedSignatureType} added to its
     *      {@link DomibusConnectorMessageDocumentType}.
     */
    public DomibusConnectorMessageType addDetachedSignatureForBusinessDocument(
        DomibusConnectorMessageType message,
        byte[] detachedSignature,
        String detachedSignatureName,
        DomibusConnectorDetachedSignatureMimeType detachedSignatureType);

    /**
     * Generates and adds a {@link DomibusConnectorMessageAttachmentType} to the
     * {@link DomibusConnectorMessageType}.
     *
     * @param message                       - the message object
     * @param businessAttachmentIdentifier  - The identifier of the given business attachment.
     * @param businessAttachment            - The business attachment as bytes.
     * @param businessAttachmentName        - The name of the business attachment.
     * @param businessAttachmentMimeType    - the mime type of the business attachment
     * @param businessAttachmentDescription - The description of the given business attachment.
     * @return The message object with a {@link DomibusConnectorMessageAttachmentType} added
     */
    public DomibusConnectorMessageType addBusinessAttachmentAsBinaryToMessage(
        DomibusConnectorMessageType message,
        String businessAttachmentIdentifier,
        byte[] businessAttachment,
        String businessAttachmentName,
        String businessAttachmentMimeType,
        String businessAttachmentDescription);

    /**
     * Generates and adds a {@link DomibusConnectorMessageAttachmentType} to the
     * {@link DomibusConnectorMessageType}.
     *
     * @param message                       - the message object
     * @param businessAttachmentIdentifier  - The identifier of the given business attachment.
     * @param businessAttachment            - The business attachment as stream object.
     * @param businessAttachmentName        - The name of the business attachment.
     * @param businessAttachmentMimeType    - the mime type of the business attachment
     * @param businessAttachmentDescription - The description of the given business attachment.
     * @return The message object with a {@link DomibusConnectorMessageAttachmentType} added
     */
    public DomibusConnectorMessageType addBusinessAttachmentAsStreamToMessage(
        DomibusConnectorMessageType message,
        String businessAttachmentIdentifier,
        DataHandler businessAttachment,
        String businessAttachmentName,
        String businessAttachmentMimeType,
        String businessAttachmentDescription);

    /**
     * Creates a new {@link DomibusConnectorMessageType} and adds the
     * {@link DomibusConnectorMessageDetailsType}. Also creates a
     * {@link DomibusConnectorMessageConfirmationType}, sets the confirmationType and adds it to the
     * message. The confirmation itself will be generated by the domibusConnector. All additional
     * parameters required for the message will also be set by the domibusConnector.
     *
     * @param ebmsMessageId    - The EBMS message id of the message this confirmationMessage refers
     *                         to. Will be set as refToMessageId.
     * @param conversationId   - The conversation of the message this confirmationMessage refers
     *                         to.
     * @param businessService  - The {@link DomibusConnectorServiceType} of the message this
     *                         confirmationMessage refers to.
     * @param businessAction   - The business action representing the confirmationType of this
     *                         confirmationMessage. Must be defined by the use case (PModes).
     * @param fromParty        - The {@link DomibusConnectorPartyType} of the receiving Party
     *                         (toParty) of the message this confirmationMessage refers to.
     * @param toParty          - The {@link DomibusConnectorPartyType} of the sending Party
     *                         (fromParty) of the message this confirmationMessage refers to.
     * @param finalRecipient   - The electronic identifier of the original sender of the message
     *                         this confirmationMessage refers to.
     * @param originalSender   - The electronic identifier of the final recipient of the message
     *                         this confirmationMessage refers to.
     * @param confirmationType - The type of confirmation.
     * @return A new message object with a {@link DomibusConnectorMessageDetailsType} and
     *      {@link DomibusConnectorMessageConfirmationType} added.
     */
    DomibusConnectorMessageType createNewConfirmationMessage(
        String ebmsMessageId,
        String conversationId,
        DomibusConnectorServiceType businessService,
        String businessAction,
        DomibusConnectorPartyType fromParty,
        DomibusConnectorPartyType toParty,
        String finalRecipient,
        String originalSender,
        DomibusConnectorConfirmationType confirmationType);
}
