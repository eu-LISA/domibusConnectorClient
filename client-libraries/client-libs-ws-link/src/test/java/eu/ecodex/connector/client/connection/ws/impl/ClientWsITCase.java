
/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.connection.ws.impl;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class ClientWsITCase {

    //TODO: start backend
    //TODO: start client
    
//    static ApplicationContext backendContext;
//    static ApplicationContext clientContext;
//    
//    @BeforeClass
//    public static void beforeClass() throws InterruptedException {
//        String[] serverProfiles = new String[] {};
//        String[] serverProperties = new String[] {"server.port=0"};
//        backendContext = BackendServer.startSpringApplication(serverProfiles, serverProperties);
//        
//        String port = backendContext.getEnvironment().getProperty("local.server.port");
//        String backendAddress = "http://localhost:" + port + "/services/backend";
//        System.out.println("Backend address is: " + backendAddress);
//        
//        
//        String[] clientProfiles = new String[] {};
//        String[] clientProperties = new String[] {"server.port=0", 
//            "connector.backend.ws.address=" + backendAddress, 
//            "ws.backendclient.name=bob" };
//        
//        clientContext = BackendClient.startSpringApplication(clientProfiles, clientProperties);
//        
//        //Thread.sleep(20000);
//    }
//    
//    List<DomibusConnectorMessage> messages;
//    
//    DomibusConnectorClientWsTransportMessageService transportWs;
//    
//    @Before
//    public void setUp() {
//        messages = backendContext.getBean("submittedMessages", List.class);
//        transportWs = clientContext.getBean(DomibusConnectorClientWsTransportMessageService.class);
//    }
//    
//    @Test
//    public void testSendMessageToServer() {
//        DomibusConnectorMessageType message = TransitionCreator.createMessage();      
//        DomibsConnectorAcknowledgementType submitMessage = transportWs.submitMessage(message);
//        
//        assertThat(submitMessage.getMessageId()).isNotNull();
//    }
//    
//    @Test
//    public void testSendNonDeliveryMessageToServer() {
//        DomibusConnectorMessageType message = TransitionCreator.createEvidenceNonDeliveryMessage();
//        
//        DomibsConnectorAcknowledgementType submitMessage = transportWs.submitMessage(message);
//        
//        assertThat(submitMessage.getMessageId()).isNotNull();
//    }
//    
//    
//    @Test
//    public void testRequestMessagesFromServer() {
//        List<DomibusConnectorMessageType> fetchMessages = transportWs.fetchMessages();
//
//        assertThat(fetchMessages).hasSize(1);
//    }
    
    
    
    
}
