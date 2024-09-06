
/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.connection.ws.linktest.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@SpringBootApplication(scanBasePackageClasses = BackendServer.class)
@Configuration
@ImportResource("classpath:/testservice.xml")
public class BackendServer {

//    private final static Logger LOGGER = LoggerFactory.getLogger(BackendServer.class);
//    
//    public static ApplicationContext startSpringApplication(String[] profiles, String[] properties) {
//
//        boolean web = true;
//        
//        SpringApplicationBuilder builder = new SpringApplicationBuilder();
//        SpringApplication springApp = builder.bannerMode(Banner.Mode.OFF)
//                .sources(BackendServer.class)
//                .properties(properties)
//                .profiles(profiles)
//                .web(web)
//                .build();
//
//        ConfigurableApplicationContext appContext = springApp.run();
//        
//        return appContext;
//    }
//    
//    @Bean("submittedMessages")
//    public List<DomibusConnectorMessage> submittedMessages() {
//        return Collections.synchronizedList(new ArrayList<>());
//    }
//    
//    @Bean("connectorBackendImpl")
//    public DomibusConnectorBackendWebService domibusConnectorBackendWebService() {
//        
//        List<DomibusConnectorMessage> submittedMessages = submittedMessages();
//        
//        DomibusConnectorBackendWebService backend = new DomibusConnectorBackendWebService() {
//            @Override
//            public DomibusConnectorMessagesType requestMessages(EmptyRequestType requestMessagesRequest) {
//                LOGGER.debug("#requestMessages");
//                return TransitionCreator.createMessages();
//            }
//
//            @Override
//            public DomibsConnectorAcknowledgementType submitMessage(DomibusConnectorMessageType submitMessageRequest) {
//                LOGGER.debug("#submitMessage");
//                DomibusConnectorMessage transformTransitionToDomain = DomibusConnectorDomainMessageTransformer.transformTransitionToDomain(submitMessageRequest);
//                submittedMessages.add(transformTransitionToDomain);
//                DomibsConnectorAcknowledgementType response = new DomibsConnectorAcknowledgementType();
//                response.setMessageId(UUID.randomUUID().toString());
//                response.setResult(true);
//                return response;
//            }
//            
//        };
//        
//        return backend;
//    }
    
}
