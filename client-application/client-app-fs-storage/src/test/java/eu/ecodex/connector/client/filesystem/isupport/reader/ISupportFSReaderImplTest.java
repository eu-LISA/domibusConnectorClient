/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.filesystem.isupport.reader;

import eu.ecodex.connector.client.filesystem.DomibusConnectorClientFileSystemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.File;

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
