/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client;

import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.storage.exception.DomibusConnectorClientStorageException;

public interface DomibusConnectorClientAppBackend extends DomibusConnectorClientBackend {
    /**
     * This method triggers the submission of a prepared and stored message. The message gets
     * completely loaded out of the storage. Therefore, it is important that the message entirely is
     * stored before triggering this method.
     *
     * @param storageLocation - The path in the storage where the message is placed.
     * @throws DomibusConnectorClientBackendException if there is an error in the backend operations
     *                                                of the client
     * @throws DomibusConnectorClientStorageException if there is an error related to storing or
     *                                                retrieving data in the storage
     * @throws IllegalArgumentException               if the input argument is invalid
     */
    void submitStoredClientBackendMessage(String storageLocation)
        throws DomibusConnectorClientBackendException, DomibusConnectorClientStorageException,
        IllegalArgumentException;
}
