/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.filesystem;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * The DomibusConnectorClientFileSystemUtil class provides utility methods for working with the file
 * system in the Domibus Connector Client. These methods include renaming message folders,
 * converting dates to property format, and generating message folder names.
 */
@UtilityClass
public class DomibusConnectorClientFileSystemUtil {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorClientFileSystemUtil.class);

    /**
     * Renames a message folder by moving it to a new location and updating its name.
     *
     * @param messageFolder          The original message folder to be renamed.
     * @param folderPath             The path of the original folder.
     * @param newFolderPathExtension The extension to append to the new folder path.
     * @return The renamed message folder, represented as a {@code File} object.
     * @throws DomibusConnectorClientFileSystemException if an error occurs during the folder
     *                                                   renaming process.
     */
    public static File renameMessageFolder(
        File messageFolder, String folderPath, String newFolderPathExtension)
        throws DomibusConnectorClientFileSystemException {
        var newMessageFolder = new File(folderPath + newFolderPathExtension);

        LOGGER.debug(
            "Try to rename message folder {} to {}",
            messageFolder.getAbsolutePath(), newMessageFolder.getAbsolutePath()
        );
        try {
            FileUtils.moveDirectory(messageFolder, newMessageFolder);
        } catch (IOException e1) {
            String error = "Could not rename folder "
                + messageFolder.getAbsolutePath() + " to " + newMessageFolder.getAbsolutePath();
            LOGGER.error(error, e1);
            throw new DomibusConnectorClientFileSystemException(error);
        }

        return newMessageFolder;
    }

    public static String convertDateToProperty(Date date) {
        final var dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        return dateFormat.format(date);
    }

    /**
     * Gets the folder name for a message based on its details.
     *
     * @param message   The message object containing the necessary details.
     * @param messageId The ID of the message.
     * @return The folder name for the message.
     * @throws DomibusConnectorClientFileSystemException If an error occurs while getting the folder
     *                                                   name.
     */
    public static String getMessageFolderName(DomibusConnectorMessageType message, String messageId)
        throws DomibusConnectorClientFileSystemException {
        if (messageId == null || messageId.isEmpty()) {
            throw new DomibusConnectorClientFileSystemException(
                "Neither ebmsId nor backendMessageId set in message!");
        }

        String fromPartyId = message.getMessageDetails().getFromParty().getPartyId();
        if (!StringUtils.hasLength(fromPartyId)) {
            throw new DomibusConnectorClientFileSystemException("No fromPartyId set in message!");
        }

        String toPartyId = message.getMessageDetails().getToParty().getPartyId();
        if (!StringUtils.hasLength(toPartyId)) {
            throw new DomibusConnectorClientFileSystemException("No toPartyId set in message!");
        }

        return new StringBuilder()
            .append(fromPartyId)
            .append("-")
            .append(toPartyId)
            .append("-")
            .append(messageId.replaceAll("[\\\\/:*?\"<>|]", "_"))
            .toString();
    }
}
