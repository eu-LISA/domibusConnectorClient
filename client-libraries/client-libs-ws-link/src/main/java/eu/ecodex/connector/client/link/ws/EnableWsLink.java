/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.link.ws;

import eu.ecodex.connector.client.link.ws.configuration.WsLinkAutoConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * This annotation is used to enable the WS-Link feature in a Spring Boot application. It must be
 * applied to a Spring Boot application class or a configuration class. When applied, it imports the
 * {@link WsLinkAutoConfiguration} class, which handles the configuration of WS-Link.
 *
 * <p>The {@code push} parameter is used to enable or disable push mode. When set to {@code true},
 * push mode is enabled. Push mode allows real-time updates to be pushed from the server to the
 * clients, whereas in pull mode the clients have to periodically request updates from the server.
 * By default, push mode is disabled.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(WsLinkAutoConfiguration.class)
public @interface EnableWsLink {
    /**
     * Push mode enabled.
     **/
    boolean push() default false;
}
