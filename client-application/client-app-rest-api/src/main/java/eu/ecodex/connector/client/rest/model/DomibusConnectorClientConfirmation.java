/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.rest.model;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code DomibusConnectorClientConfirmation} class represents a confirmation message received
 * by the Domibus Connector Client. It contains information about the confirmation , such as the ID,
 * confirmation type, time received, and the confirmation data itself.
 */
@Data
@NoArgsConstructor
public class DomibusConnectorClientConfirmation {
    private long id;
    private String confirmationType;
    private Date received;
    private byte[] confirmation;
}
