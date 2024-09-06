/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.filesystem;

import eu.ecodex.connector.client.storage.DomibusConnectorClientStorage;
import java.io.File;

/**
 * This interface extends the DomibusConnectorClientStorage interface and defines additional
 * operations for handling a filesystem storage. It is intended to be implemented by a custom
 * filesystem storage implementation that can be used by the client.
 *
 * @see DomibusConnectorClientStorage
 */
public interface DomibusConnectorClientFSStorage extends DomibusConnectorClientStorage {
    void setMessagesDir(File messagesDir);
}
