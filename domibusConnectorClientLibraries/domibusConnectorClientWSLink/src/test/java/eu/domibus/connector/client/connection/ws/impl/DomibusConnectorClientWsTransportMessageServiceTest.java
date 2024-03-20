
package eu.domibus.connector.client.connection.ws.impl;

/**
 *
 * @author Stephan Spindler 
 */
public class DomibusConnectorClientWsTransportMessageServiceTest {

//    private final static Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorClientWsTransportMessageServiceTest.class);
//    
//    private DomibusConnectorClientWsTransportMessageService service;
//    
//    private DomibusConnectorBackendWebService mockedWebService;
//
//    
//    @Before
//    public void setUp() {
//         
//         
//        mockedWebService = Mockito.mock(DomibusConnectorBackendWebService.class);
//        Mockito.when(mockedWebService.requestMessages(any(EmptyRequestType.class))).thenReturn(TransitionCreator.createMessages());
//         
//         
//        service = new DomibusConnectorClientWsTransportMessageService();
//        service.setWebService(mockedWebService);
//    }
//
//    @Test
//    public void testFetchMessages() {
//        
//        List<DomibusConnectorMessageType> fetchMessages = service.fetchMessages();
//        assertThat(fetchMessages).hasSize(1);
//        
//    }
//    
//    
//    @Test
//    public void testSubmitMessage() {
//        final ArrayList<DomibusConnectorMessageType> rcvMessages = new ArrayList<>();
//        
//        Mockito.when(mockedWebService.submitMessage(any(DomibusConnectorMessageType.class)))
//            .thenAnswer(new Answer<DomibsConnectorAcknowledgementType>() {
//                    @Override
//                    public DomibsConnectorAcknowledgementType answer(InvocationOnMock invocation) throws Throwable {
//                        DomibusConnectorMessageType type = invocation.getArgumentAt(0, DomibusConnectorMessageType.class);
//                        rcvMessages.add(type);
//
//                        DomibsConnectorAcknowledgementType ack = new DomibsConnectorAcknowledgementType();
//                        ack.setMessageId("MYID");
//                        ack.setResult(true);
//
//                        return ack;
//                    }        
//            });
//      
//        
//        DomibusConnectorMessageType msg = TransitionCreator.createEpoMessage();
//        
//        DomibsConnectorAcknowledgementType submitMessage = service.submitMessage(msg);
//        
//        assertThat(submitMessage.getMessageId()).isEqualTo("MYID");
//        assertThat(rcvMessages).hasSize(1);
//        
//    }
    
}