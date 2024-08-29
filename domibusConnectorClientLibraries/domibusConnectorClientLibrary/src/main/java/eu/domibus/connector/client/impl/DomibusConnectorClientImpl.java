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

import eu.domibus.connector.client.DomibusConnectorClient;
import eu.domibus.connector.client.DomibusConnectorClientMessageHandler;
import eu.domibus.connector.client.exception.DCCConnectorAcknowledgementException;
import eu.domibus.connector.client.exception.DCCContentMappingException;
import eu.domibus.connector.client.exception.DCCMessageDataInvalid;
import eu.domibus.connector.client.exception.DCCMessageValidationException;
import eu.domibus.connector.client.exception.DomibusConnectorBackendWebServiceClientException;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.link.DomibusConnectorClientLink;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * This class is an implementation of the DomibusConnectorClient interface. It provides methods for
 * submitting new messages to the connector, requesting new messages from the connector,
 * acknowledging messages, and triggering confirmation for a message.
 *
 * @see DomibusConnectorClient
 */
@SuppressWarnings("squid:S1135")
@Component
public class DomibusConnectorClientImpl implements DomibusConnectorClient {
    private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorClientImpl.class);
    public static final String BACKEND_MESSAGE_ID = "backendmessageid";
    @Autowired
    private DomibusConnectorClientLink clientService;
    @Autowired
    private DomibusConnectorClientMessageHandler messageHandler;

    @Override
    public void submitNewMessageToConnector(DomibusConnectorMessageType message)
        throws DomibusConnectorClientException {
        MDC.put(BACKEND_MESSAGE_ID, message.getMessageDetails().getBackendMessageId());
        DomibsConnectorAcknowledgementType domibusConnectorAckType;

        LOGGER.debug(
            "Preparing message [{}] for submission.",
            message.getMessageDetails().getBackendMessageId()
        );
        messageHandler.prepareOutboundMessage(message);

        try {
            LOGGER.debug(
                "Submitting message [{}]...", message.getMessageDetails().getBackendMessageId());
            domibusConnectorAckType = clientService.submitMessageToConnector(message);
        } catch (DomibusConnectorBackendWebServiceClientException e) {
            LOGGER.error("Exception submitting message to connector: ", e);
            MDC.remove(BACKEND_MESSAGE_ID);
            throw e;
        }

        if (domibusConnectorAckType == null) {
            LOGGER.error(
                "The received acknowledgement for message with backend message ID {} is null! ",
                message.getMessageDetails().getBackendMessageId()
            );
            MDC.remove(BACKEND_MESSAGE_ID);
            throw new DCCConnectorAcknowledgementException(
                "The received acknowledgement for message with backend message ID "
                    + message.getMessageDetails().getBackendMessageId() + " is null!");
        }
        if (!domibusConnectorAckType.isResult()) {
            LOGGER.error(
                "The received acknowledgement for message with backend message ID {} "
                    + "is negative! {} ",
                message.getMessageDetails().getBackendMessageId(),
                domibusConnectorAckType.getResultMessage()
            );
            MDC.remove(BACKEND_MESSAGE_ID);
            throw new DCCConnectorAcknowledgementException(
                "The received acknowledgement for message with backend message ID "
                    + message.getMessageDetails().getBackendMessageId() + " is negative!");
        }

        LOGGER.info("Message [{}] submitted.", message.getMessageDetails().getBackendMessageId());
    }

    /**
     * This method is used to request new messages from the connector. It returns a
     * {@link DomibusConnectorMessagesType} object that contains all the pending messages for the
     * client.
     *
     * @return A {@link DomibusConnectorMessagesType} object holding all the pending messages for
     *      the client.
     * @throws DomibusConnectorClientException if an error occurs while requesting messages from the
     *                                         connector.
     * @see DomibusConnectorClientException
     * @deprecated This method is deprecated and may be removed in future versions. Use alternative
     *      methods for more specific behavior.
     */
    @Override
    @Deprecated
    public DomibusConnectorMessagesType requestNewMessagesFromConnector()
        throws DomibusConnectorClientException {
        DomibusConnectorMessagesType messages;
        try {
            messages = clientService.requestMessagesFromConnector();
        } catch (DomibusConnectorBackendWebServiceClientException e) {
            LOGGER.error("Exception occurred requesting new messages from connector!");
            throw e;
        }

        var mappedMessages = new DomibusConnectorMessagesType();

        if (messages != null && !CollectionUtils.isEmpty(messages.getMessages())) {
            LOGGER.debug(
                "{} new messages from connector to transport to client...",
                messages.getMessages().size()
            );
            for (DomibusConnectorMessageType message : messages.getMessages()) {
                if (message.getMessageContent() != null) {
                    try {
                        messageHandler.prepareInboundMessage(message);
                    } catch (DCCMessageValidationException | DCCContentMappingException e1) {
                        LOGGER.error(e1);
                        e1.printStackTrace();
                        continue;
                    }
                }
                mappedMessages.getMessages().add(message);
            }
        }

        return mappedMessages;
    }

    @Override
    public Map<String, DomibusConnectorMessageType> requestNewMessagesFromConnector(
        Integer maxFetchCount, boolean acknowledgeAutomatically)
        throws DomibusConnectorClientException {
        Map<String, DomibusConnectorMessageType> receivedMessages = new HashMap<>();

        List<String> pendingMessageTransportIds;
        try {
            pendingMessageTransportIds = clientService.listPendingMessages();
        } catch (DomibusConnectorBackendWebServiceClientException e) {
            LOGGER.error(
                "Exception occurred requesting list of pending message transport IDs "
                    + "from connector!"
            );
            throw e;
        }

        if (!CollectionUtils.isEmpty(pendingMessageTransportIds)) {
            LOGGER.debug(
                "Received {} pending message transport IDs.", pendingMessageTransportIds.size());
            if (maxFetchCount != null && maxFetchCount > 0
                && pendingMessageTransportIds.size() > maxFetchCount) {

                pendingMessageTransportIds =
                    pendingMessageTransportIds.subList(0, maxFetchCount - 1);
                LOGGER.debug(
                    "Stripped list of pending message transport IDs to {} due to maxFetchCount.",
                    pendingMessageTransportIds.size()
                );
            }

            for (var pendingMessageTransportId : pendingMessageTransportIds) {
                DomibusConnectorMessageType message;
                try {
                    message = clientService.getMessageById(pendingMessageTransportId);
                } catch (DomibusConnectorBackendWebServiceClientException e) {
                    // In case a message cannot be received from the connector, the loop continues
                    // and only an error message in the logs will appear.
                    // The message transport ID will then most likely be contained in the next call
                    // of listPendingMessages.
                    LOGGER.error(
                        "Exception occurred requesting message with transport id {} "
                            + "from connector!",
                        pendingMessageTransportId
                    );
                    continue;
                }

                if (message.getMessageContent() != null) {
                    try {
                        messageHandler.prepareInboundMessage(message);
                    } catch (DCCMessageValidationException | DCCContentMappingException e1) {
                        LOGGER.error(e1);
                        e1.printStackTrace();
                        continue;
                    }
                }

                receivedMessages.put(pendingMessageTransportId, message);

                if (acknowledgeAutomatically) {
                    var messageResponseType =
                        new DomibusConnectorMessageResponseType();

                    messageResponseType.setResult(true);
                    messageResponseType.setResponseForMessageId(pendingMessageTransportId);
                    try {
                        clientService.acknowledgeMessage(messageResponseType);
                    } catch (DomibusConnectorBackendWebServiceClientException e) {
                        LOGGER.error(
                            "Exception occurred auto-acknowledge pending message with "
                                + "message transport id {} to connector!",
                            messageResponseType.getResponseForMessageId(), e
                        );
                    }
                }
            }
        }
        return receivedMessages;
    }

    @Override
    public void acknowledgeMessage(
        String messageTransportId, boolean result, String backendMessageId) {
        var messageResponseType = new DomibusConnectorMessageResponseType();

        messageResponseType.setResult(result);
        messageResponseType.setResponseForMessageId(messageTransportId);
        messageResponseType.setAssignedMessageId(backendMessageId);

        try {
            clientService.acknowledgeMessage(messageResponseType);
        } catch (DomibusConnectorBackendWebServiceClientException e) {
            LOGGER.error(
                "Exception occurred acknowledge pending message with message transport "
                    + "id {} to connector!",
                messageResponseType.getResponseForMessageId(), e
            );
        }
    }

    @Override
    public void triggerConfirmationForMessage(DomibusConnectorMessageType confirmationMessage)
        throws DomibusConnectorClientException {

        String refToMessageId = confirmationMessage.getMessageDetails() != null
            ? confirmationMessage.getMessageDetails().getRefToMessageId()
            : null;

        if (confirmationMessage.getMessageDetails() == null || refToMessageId == null
            || refToMessageId.isEmpty()) {
            throw new DCCMessageDataInvalid(
                "The field [refToMessageId] in the messageDetails of the confirmationMessage must "
                    + "not be null! It must contain the ebmsId of the originalMessage that should "
                    + "be confirmed!"
            );
        }

        if (confirmationMessage.getMessageConfirmations() == null
            || confirmationMessage.getMessageConfirmations().getFirst() == null
            || confirmationMessage.getMessageConfirmations().getFirst()
                                  .getConfirmationType() == null) {
            throw new DCCMessageDataInvalid(
                "The confirmationMessage must contain one messageConfirmation. This "
                    + "messageConfirmation must contain the confirmationType that should be "
                    + "generated and submitted by the connector!"
            );
        }
        DomibusConnectorConfirmationType confirmationType =
            confirmationMessage.getMessageConfirmations().getFirst().getConfirmationType();

        confirmationMessage.setMessageDetails(
            setDummyValuesForConfirmationTrigger(confirmationMessage.getMessageDetails()));

        var domibusConnectorAckType = new DomibsConnectorAcknowledgementType();
        try {
            LOGGER.debug(
                "Submitting confirmation message with refToMessageId {} and "
                    + "confirmationType {} to connector.",
                refToMessageId, confirmationType.name()
            );
            domibusConnectorAckType.setResult(
                true); // when no exception is thrown message is assumed processed successfully!
            domibusConnectorAckType = clientService.submitMessageToConnector(confirmationMessage);
        } catch (DomibusConnectorBackendWebServiceClientException e) {
            LOGGER.error("Exception submitting confirmation message to connector: ", e);
            throw e;
        }

        if (domibusConnectorAckType == null) {
            LOGGER.error(
                "The received acknowledgement for confirmation message with originalEbmsId {} "
                    + "and confirmationType {} is null! ", refToMessageId, confirmationType.name()
            );
            throw new DCCConnectorAcknowledgementException(
                "The received acknowledgement for confirmation message with originalEbmsId "
                    + refToMessageId + " and confirmationType " + confirmationType.name()
                    + " is null!"
            );
        }
        if (!domibusConnectorAckType.isResult()) {
            LOGGER.error(
                "The received acknowledgement for confirmation message with "
                    + "originalEbmsId {} and confirmationType {} is negative! \n"
                    + "ResultMessage: {}", refToMessageId, confirmationType.name(),
                domibusConnectorAckType.getResultMessage()
            );
            throw new DCCConnectorAcknowledgementException(
                "The received acknowledgement for confirmation message with originalEbmsId "
                    + refToMessageId + " and confirmationType " + confirmationType.name()
                    + " is negative! \n"
                    + "ResultMessage: " + domibusConnectorAckType.getResultMessage());
        }
    }

    // Workaround for domibusConnector versions 4.2.x and 4.3.x as it checks the presence of
    // Parties when mapping transition to domain model
    // TODO: make it possible that the following empty action, service, parties are not required
    //  for evidence
    // trigger message! if refToMessageId is set!
    private DomibusConnectorMessageDetailsType setDummyValuesForConfirmationTrigger(
        DomibusConnectorMessageDetailsType messageDetails) {
        var dummyParty = new DomibusConnectorPartyType();
        dummyParty.setPartyId("DUMMY");

        messageDetails.setAction(new DomibusConnectorActionType());
        messageDetails.getAction().setAction("DummyAction");
        messageDetails.setService(new DomibusConnectorServiceType());
        messageDetails.getService().setService("DummyService");
        messageDetails.setFromParty(dummyParty);
        messageDetails.setToParty(dummyParty);
        messageDetails.setFinalRecipient("dummyRecipient");
        messageDetails.setOriginalSender("dummySender");

        return messageDetails;
    }
}
