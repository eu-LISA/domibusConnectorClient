package eu.domibus.connector.client.filesystem.isupport.reader;

import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemException;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemReader;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@ActiveProfiles("iSupport")
class ISupportFSReaderImplTest {

    @Autowired
    private ISupportFSReaderImpl sut;

    @Autowired
    private DomibusConnectorClientFileSystemReader sut2;

    @SpringBootApplication(
            scanBasePackages = {"eu.domibus.connector.client.filesystem"}
    )
    public static class TestContext {}

    @Autowired
    private ResourceLoader resourceLoader;

    private File testFolder;

//    @org.junit.jupiter.api.Test
//    void processMessageFolderFiles() throws DomibusConnectorClientFileSystemException {
//        final File testdata = new File("testdata");
//        final DomibusConnectorMessageType domibusConnectorMessageType = sut.processMessageFolderFiles(testFolder);
//
//        assertThat(domibusConnectorMessageType.getMessageDetails().getOriginalSender()).isEqualTo("Pellet Jean-Marc");
//        assertThat(domibusConnectorMessageType.getMessageContent()).isNotNull();
//    }
//
//    @org.junit.jupiter.api.Test
//    void readMessages() throws DomibusConnectorClientFileSystemException {
//        final File testdata = new File("testdata");
//        final DomibusConnectorMessageType domibusConnectorMessageType = sut2.readMessageFromFolder(testFolder);
//
//        assertThat(domibusConnectorMessageType.getMessageDetails().getOriginalSender()).isEqualTo("Pellet Jean-Marc");
//        assertThat(domibusConnectorMessageType.getMessageContent()).isNotNull();
//    }
//
//
//    @org.junit.jupiter.api.Test
//    void loadFileContent() throws DomibusConnectorClientFileSystemException {
//        final File testdata = new File("testdata");
//        final byte[] bytes = sut2.loadFileContentFromMessageFolder(testFolder, "2022-01-19-Convention_Request_for_Specific_Measures_Article_71-SM01.pdf");
//
//        assertThat(bytes).isNotNull();
//    }
//
//
//    @org.junit.jupiter.api.Test
//    void readUnsentMessages() throws DomibusConnectorClientFileSystemException {
//        final File testdata = new File("testdata");
//        final List<File> files = sut2.readUnsentMessages(testFolder);
//
//        assertThat(files).isEmpty();
//    }
//
//
//    @BeforeEach
//    void init() throws IOException {
//        testFolder = resourceLoader.getResource("classpath:testdata").getFile();
//    }
}