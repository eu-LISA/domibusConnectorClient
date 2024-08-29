/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.mapping;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * Interface with methods to map the businessContent of a {@link DomibusConnectorMessageType}
 * message. The businessContent is placed inside the {@link DomibusConnectorMessageContentType} of a
 * message. If implemented and the implementation is on the classpath, then it will be instantiated
 * automatically.
 *
 * @author riederb
 */
public interface DomibusConnectorClientContentMapper {
    /**
     * Method to map the businessContent of an inbound message before storing the message at the
     * client side. After the mapping, if successful, the businessContent inside the inbound message
     * will be replaced with the mapped content.
     *
     * <p>The default implementation, if not overridden, will result in doing nothing. Meaning,
     * that the businessContent will remain the same after the mapping is called.
     *
     * @param message - a {@link DomibusConnectorMessageType} object containing the received
     *                businessContent inside the {@link DomibusConnectorMessageContentType}.
     * @return The {@link DomibusConnectorMessageType} with the mapped businessContent inside the
     *      {@link DomibusConnectorMessageContentType}.
     * @throws DomibusConnectorClientContentMapperException if an exception occurs during the
     *                                                      mapping process.
     */
    DomibusConnectorMessageType mapInboundBusinessContent(
        DomibusConnectorMessageType message) throws DomibusConnectorClientContentMapperException;

    /**
     * Method to map the businessContent of an outbound message before sending the message to the
     * domibusConnector. After the mapping, if successful, the businessContent inside the outbound
     * message will be replaced with the mapped content.
     *
     * <p>The default implementation, if not overridden, will result in doing nothing. Meaning,
     * that the businessContent will remain the same after the mapping is called.
     *
     * @param message - a {@link DomibusConnectorMessageType} object containing the original
     *                businessContent inside the {@link DomibusConnectorMessageContentType}.
     * @return The {@link DomibusConnectorMessageType} with the mapped businessContent inside the
     *      {@link DomibusConnectorMessageContentType}.
     * @throws DomibusConnectorClientContentMapperException if an exception occurs during the
     *                                                      mapping process.
     */
    DomibusConnectorMessageType mapOutboundBusinessContent(
        DomibusConnectorMessageType message) throws DomibusConnectorClientContentMapperException;
}
