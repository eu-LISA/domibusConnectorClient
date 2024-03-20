package eu.domibus.connector.client.filesystem.isupport.sbdh;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.filesystem.isupport.reader.ISupportFSReaderImpl;
import eu.domibus.connector.client.filesystem.isupport.sbdh.model.StandardBusinessDocumentHeader;

@Component
@Validated
@Valid
@Profile("iSupport")
public class SBDHJaxbConverter {
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ISupportFSReaderImpl.class);
	
	private Unmarshaller jaxbMarshaller;
	
	public StandardBusinessDocumentHeader getSBDH(File message, String messagePropertiesFileName) throws JAXBException, IOException {
		if(this.jaxbMarshaller == null) {
			createUnmarshaller();
		}

		String pathname = message.getAbsolutePath() + File.separator + messagePropertiesFileName;
		LOGGER.debug("#getSBDH: Loading SBDH from file {}", pathname);
		File file = new File(pathname);
		if (!file.exists()) {
			LOGGER.error("#getSBDH: SBDH file '" + file.getAbsolutePath()
			+ "' does not exist. Message cannot be processed!");
			return null;
		}

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        if (fileInputStream.read(data) == 0) {
            LOGGER.trace("Data read OK");
        }
        fileInputStream.close();

        return (StandardBusinessDocumentHeader) jaxbMarshaller.unmarshal(new
                ByteArrayInputStream(new String(data).getBytes(StandardCharsets.UTF_8)));
    }
	
	private void createUnmarshaller() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(StandardBusinessDocumentHeader.class);
        Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
        this.jaxbMarshaller =  jaxbMarshaller;
	}

}
