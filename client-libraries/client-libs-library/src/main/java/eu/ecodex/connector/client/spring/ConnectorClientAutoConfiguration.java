/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.spring;

import eu.ecodex.connector.client.schema.validation.SeverityLevel;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

/**
 * This class is a configuration class for the ConnectorClient library. It is used to configure
 * properties and beans related to the library.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Configuration
@ConfigurationProperties(prefix = ConnectorClientAutoConfiguration.PREFIX)
@PropertySource("classpath:/connector-client-library-default.properties")
@Validated
@Valid
@Data
public class ConnectorClientAutoConfiguration {
    public static final String PREFIX = "connector-client.library";
    @Nullable
    private SeverityLevel schemaValidationMaxSeverityLevel;
    private boolean acknowledgeMessagesRequired = false;
    @Nullable
    private Integer requestMessagesMaxCount;
}
