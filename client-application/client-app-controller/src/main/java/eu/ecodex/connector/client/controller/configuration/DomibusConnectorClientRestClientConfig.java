/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.controller.configuration;

import eu.ecodex.connector.client.controller.rest.impl.DomibusConnectorClientDeliveryRestClient;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The DomibusConnectorClientRestClientConfig class represents the configuration properties for the
 * Domibus connector client REST client. The properties include the URL, enabled status, and method
 * URLs for delivering new messages and confirmations.
 */
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = DomibusConnectorClientRestClientConfig.PREFIX)
@PropertySource("classpath:/connector-client-controller-default.properties")
public class DomibusConnectorClientRestClientConfig {
    public static final String PREFIX =
        DomibusConnectorClientControllerConfig.PREFIX + ".delivery-rest-client";
    @NotNull
    private String url;
    @NotNull
    private boolean enabled;
    @NotNull
    private String deliverNewMessageMethodUrl;
    @NotNull
    private String deliverNewConfirmationMethodUrl;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl(url).build();
    }

    /**
     * This method creates and configures a {@code DomibusConnectorClientDeliveryRestClient} object.
     * If the {@code enabled} configuration property is set to false, null is returned.
     *
     * @return A {@code DomibusConnectorClientDeliveryRestClient} object if enabled is true,
     *      otherwise null.
     */
    @Bean
    public DomibusConnectorClientDeliveryRestClient deliveryRestClient() {
        if (!enabled) {
            return null;
        }
        var restClient = new DomibusConnectorClientDeliveryRestClient();
        restClient.setDeliveryRestClient(webClient(WebClient.builder()));
        restClient.setDeliverNewConfirmationMethodUrl(deliverNewConfirmationMethodUrl);
        restClient.setDeliverNewMessageMethodUrl(deliverNewMessageMethodUrl);

        return restClient;
    }
}
