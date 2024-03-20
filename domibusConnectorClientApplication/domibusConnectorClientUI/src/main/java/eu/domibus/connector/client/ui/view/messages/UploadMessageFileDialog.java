package eu.domibus.connector.client.ui.view.messages;

import java.io.ByteArrayOutputStream;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFileType;
import eu.domibus.connector.client.ui.component.LumoLabel;

public class UploadMessageFileDialog extends Dialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ComboBox<DomibusConnectorClientMessageFileType> fileType;
	byte[] fileContents = null;
	String fileName;
	
	LumoLabel resultLabel;
	Div areaResult;
	
	public UploadMessageFileDialog() {
		Div headerContent = new Div();
		Label header = new Label("Upload file to message");
		header.getStyle().set("font-weight", "bold");
		header.getStyle().set("font-style", "italic");
		headerContent.getStyle().set("text-align", "center");
		headerContent.getStyle().set("padding", "10px");
		headerContent.add(header);
		add(headerContent);
		
		MemoryBuffer  buffer = new MemoryBuffer ();
		Upload upload = new Upload(buffer);

		Div areaFileType = new Div();
		fileType = new ComboBox<DomibusConnectorClientMessageFileType>();
		fileType.setItems(DomibusConnectorClientMessageFileType.values());
		fileType.setLabel("File Type");
		fileType.setWidth("300px");
		fileType.addValueChangeListener(e -> {
			if(e.getValue()!=null) {
				if(e.getValue().equals(DomibusConnectorClientMessageFileType.BUSINESS_CONTENT) ||
						e.getValue().equals(DomibusConnectorClientMessageFileType.DETACHED_SIGNATURE)) {
					upload.setAcceptedFileTypes("application/xml", "text/xml");
					fileType.setInvalid(false);
					upload.setVisible(true);
				}else
				if(e.getValue().equals(DomibusConnectorClientMessageFileType.CONFIRMATION)) {
					fileType.setErrorMessage("Invalid file type: Confirmations must not be uploaded as part of the message! If a confirmation should be part of this message, add it as BUSINESS_ATTACHMENT!");
					fileType.setInvalid(true);
					upload.setVisible(false);
				}else
				if(e.getValue().equals(DomibusConnectorClientMessageFileType.BUSINESS_DOCUMENT)) {
					upload.setAcceptedFileTypes("application/pdf");
					fileType.setInvalid(false);
					upload.setVisible(true);
				}else {
					upload.setAcceptedFileTypes();
					fileType.setInvalid(false);
					upload.setVisible(true);
				}
				
			}
		});
		
		areaFileType.add(fileType);
		
		add(areaFileType);
		
		Div areaImporter = new Div();
		
		
		upload.setMaxFiles(1);
		upload.setId("File-Upload");
//		upload.setAcceptedFileTypes("application/xml", "text/xml");
		upload.setVisible(false);

		areaResult = new Div();
		
		resultLabel = new LumoLabel();
		
		upload.addSucceededListener(event -> {
			fileContents = ((ByteArrayOutputStream) buffer.getFileData().getOutputBuffer())
                            .toByteArray();
			fileName = buffer.getFileName();
		   resultLabel.setText("File uploaded");
			resultLabel.getStyle().set("color", "green");
			areaResult.add(resultLabel);
		});
		upload.addFailedListener(e -> {
			resultLabel.setText("File upload failed!");
			resultLabel.getStyle().set("color", "red");
			areaResult.add(resultLabel);
		});
		
		
		areaImporter.add(upload);
		
		add(areaImporter);
		add(areaResult);
	}
	
	public void setErrorText(String errorMessage) {
		resultLabel.setText(errorMessage);
		resultLabel.getStyle().set("color", "red");
		areaResult.add(resultLabel);
	}

	public ComboBox<DomibusConnectorClientMessageFileType> getFileType() {
		return fileType;
	}

	public void setFileType(ComboBox<DomibusConnectorClientMessageFileType> fileType) {
		this.fileType = fileType;
	}

	public byte[] getFileContents() {
		return fileContents;
	}

	public void setFileContents(byte[] fileContents) {
		this.fileContents = fileContents;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	

}
