/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.rest;

import eu.ecodex.connector.client.rest.exception.MessageNotFoundException;
import eu.ecodex.connector.client.rest.exception.MessageSubmissionException;
import eu.ecodex.connector.client.rest.exception.ParameterException;
import eu.ecodex.connector.client.rest.exception.StorageException;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageList;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This interface is a public interface for the domibusConnectorClient-Application to be approached
 * from outside. It is used by the domibusConnectorClient-UI to connect to and exchange data with
 * the application.
 *
 * @author riederb
 */
@RequestMapping(DomibusConnectorClientRestAPI.RESTSERVICE_PATH)
public interface DomibusConnectorClientRestAPI {
    /**
     * This static String is the relative path where this REST service can be reached.
     */
    String RESTSERVICE_PATH = "/restservice";

    /**
     * Method to receive all messages stored in the database of the
     * domibusConnectorClient-Application.
     *
     * @return An object holding a List of message objects.
     */
    @GetMapping("/getAllMessages")
    DomibusConnectorClientMessageList getAllMessages();

    /**
     * Method to receive a certain message stored in the domibusConnectorClient-Application
     * database.
     *
     * @param id - the database id (technical key) of the message. Usually not used to display
     *           message data.
     * @return The message object if id is present.
     * @throws MessageNotFoundException if the message with the given id is not present.
     */
    @GetMapping("/getMessageById")
    DomibusConnectorClientMessage getMessageById(@RequestParam Long id)
        throws MessageNotFoundException;

    /**
     * Method to receive a certain message stored in the domibusConnectorClient-Application
     * database.
     *
     * @param backendMessageId - the backend message id of the message.
     * @return The message object if the backend message id is present.
     * @throws MessageNotFoundException if the message with the given the backend message id is not
     *                                  present.
     */
    @GetMapping("/getMessageByBackendMessageId")
    DomibusConnectorClientMessage getMessageByBackendMessageId(
        @RequestParam String backendMessageId) throws MessageNotFoundException;

    /**
     * Method to receive a certain message stored in the domibusConnectorClient-Application
     * database.
     *
     * @param ebmsMessageId - the ebmsMessageId of the message.
     * @return The message object if the ebmsMessageId is present.
     * @throws MessageNotFoundException if the message with the given ebmsMessageId is not present.
     */
    @GetMapping("/getMessageByEbmsMessageId")
    DomibusConnectorClientMessage getMessageByEbmsMessageId(@RequestParam String ebmsMessageId)
        throws MessageNotFoundException;

    /**
     * Method to receive all messages from the conversation with the given id.
     *
     * @param conversationId - the conversationId of the conversation
     * @return An object holding a List of message objects.
     * @throws MessageNotFoundException if no message with the given conversationId is present.
     */
    @GetMapping("/getMessagesByConversationId")
    DomibusConnectorClientMessageList getMessagesByConversationId(
        @RequestParam String conversationId) throws MessageNotFoundException;

    /**
     * Method to receive all messages within the given period.
     *
     * @param from - start date of the period
     * @param to   - end date of the period
     * @return An object holding a List of message objects.
     * @throws MessageNotFoundException if no message in the given period is present.
     */
    @GetMapping("/getMessagesByPeriod")
    DomibusConnectorClientMessageList getMessagesByPeriod(
        @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Date from,
        @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Date to)
        throws MessageNotFoundException;

    /**
     * With this method a certain file from the configured storage can be loaded as bytes.
     *
     * @param storageLocation - The full path to the storage location where the file is stored.
     * @param fileName        - The name of the file as it is stored within the storage.
     * @return the bytes of the file if present.
     * @throws ParameterException if the file cannot be found.
     */
    @GetMapping("/loadFileContentFromStorage")
    byte[] loadFileContentFromStorage(
        @RequestParam String storageLocation, @RequestParam String fileName)
        throws ParameterException;

    /**
     * With this method a message can either be created (if not done yet), or updated. This is for
     * the data contained within the message object. It does not save message attachments such as
     * files.
     *
     * @param message - The message object with the new data contained.
     * @return The message object after the update happened. If the message is new it is enriched by
     *      the database id.
     * @throws ParameterException if the message object was not filled properly.
     * @throws StorageException   if the message could not be stored in the storage.
     */
    @PostMapping(
        value = "/saveMessage", consumes = "application/json", produces = "application/json"
    )
    DomibusConnectorClientMessage saveMessage(@RequestBody DomibusConnectorClientMessage message)
        throws ParameterException, StorageException;

    /**
     * Method to add a new file to the message. The message needs to be saved already in the
     * database.
     *
     * @param messageFile - The object holding all necessary information and the bytes of the file
     *                    to be stored.
     * @return A Boolean representing if the operation was successful.
     * @throws ParameterException if the messageFile parameter was not sufficiently filled.
     * @throws StorageException   if there was an exception at storage level.
     */
    @PostMapping(
        value = "/uploadMessageFile", consumes = "application/json", produces = "application/json"
    )
    Boolean uploadMessageFile(@RequestBody DomibusConnectorClientMessageFile messageFile)
        throws ParameterException, StorageException;

    /**
     * Deletes a message. The message is deleted from the database of the
     * domibusConnectorClient-Application. Also in the storage the message is deleted.
     *
     * @param id - The database id (technical key) of the message to be deleted.
     * @return A Boolean representing if the operation was successful.
     * @throws ParameterException if the given id is not present.
     * @throws StorageException   if the stored storage location cannot be found or the stored
     *                            message cannot be deleted.
     */
    @PostMapping(
        value = "/deleteMessageById", consumes = "application/json", produces = "application/json"
    )
    Boolean deleteMessageById(@RequestBody Long id) throws ParameterException, StorageException;

    /**
     * Method to submit an already stored and prepared message to the connector. To be successful
     * the message needs to be prepared entirely. This method relies on the storage. So, the message
     * to be submitted, is loaded entirely from the storage.
     *
     * @param message - The message object of the message to be submitted to the domibusConnector.
     * @return A Boolean representing if the operation was successful.
     * @throws ParameterException         if the message cannot be found.
     * @throws StorageException           if the stored storage location cannot be found or the
     *                                    stored message cannot be loaded from storage.
     * @throws MessageSubmissionException if an error occurred submitting the message.
     */
    @PostMapping(
        value = "/submitStoredClientMessage", consumes = "application/json",
        produces = "application/json"
    )
    Boolean submitStoredClientMessage(@RequestBody DomibusConnectorClientMessage message)
        throws ParameterException, StorageException, MessageSubmissionException;

    /**
     * Deletes a file from a message. The file is deleted from the storage of the message.
     *
     * @param messageFile - The object holding all necessary information of the file to be deleted.
     * @return A Boolean representing if the operation was successful.
     * @throws ParameterException if the given file is not present.
     * @throws StorageException   if the stored storage location cannot be found or the stored file
     *                            cannot be deleted.
     */
    @PostMapping(
        value = "/deleteMessageFile", consumes = "application/json", produces = "application/json"
    )
    Boolean deleteMessageFile(@RequestBody DomibusConnectorClientMessageFile messageFile)
        throws ParameterException, StorageException;
}
