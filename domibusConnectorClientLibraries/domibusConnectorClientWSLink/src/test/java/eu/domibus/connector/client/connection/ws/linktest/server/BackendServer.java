
package eu.domibus.connector.client.connection.ws.linktest.server;

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
