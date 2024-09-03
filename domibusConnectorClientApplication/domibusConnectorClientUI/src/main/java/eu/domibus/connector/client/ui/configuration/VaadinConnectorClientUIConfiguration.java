/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.ui.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * This class is a configuration class for setting up the WebClient for the Vaadin Connector Client
 * UI. It is responsible for configuring the base URL of the WebClient using the provided
 * connector-client-rest-url property value from the application configuration.
 */
@Configuration
public class VaadinConnectorClientUIConfiguration {
    @Value("${connector-client-rest-url}")
    private String connectorClientRestURL;

    @Bean
    public WebClient restClient(WebClient.Builder builder) {
        return builder.baseUrl(connectorClientRestURL).build();
    }
}
