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

import lombok.NoArgsConstructor;

/**
 * The DomibusConnectorClientContentMapperException class is an exception that is thrown when there
 * is an error during the content mapping process in a Domibus Connector Client.
 */
@NoArgsConstructor
public class DomibusConnectorClientContentMapperException extends Exception {
    private static final long serialVersionUID = -7493794302702644307L;

    public DomibusConnectorClientContentMapperException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DomibusConnectorClientContentMapperException(String arg0) {
        super(arg0);
    }

    public DomibusConnectorClientContentMapperException(Throwable arg0) {
        super(arg0);
    }
}
