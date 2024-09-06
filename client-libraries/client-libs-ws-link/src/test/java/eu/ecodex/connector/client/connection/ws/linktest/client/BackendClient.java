
/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.connection.ws.linktest.client;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@SpringBootApplication(scanBasePackages = "eu.ecodex.connector.client.connection")
@Configuration
//@Import(DomibusConnectorClientWebServiceLinkConfiguration.class)
public class BackendClient {

    
    public static ApplicationContext startSpringApplication(String[] profiles, String[] properties) {

        //TODO: edit for push test
        boolean web;
        List<String> p = Arrays.asList(profiles);
        web = p.contains("push"); //if profile is push start webserver for push interface
                    
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        SpringApplication springApp = builder.bannerMode(Banner.Mode.OFF)
                .sources(BackendClient.class)
                .properties(properties)
                .profiles(profiles)
                .web(WebApplicationType.SERVLET)
                .build();

        ConfigurableApplicationContext appContext = springApp.run();
        
        return appContext;
    }
    
    
}
