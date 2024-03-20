package eu.domibus.connector.client.ui.view.messages;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.client.ui.component.LumoLabel;
import eu.domibus.connector.client.ui.form.DynamicMessageForm;
import eu.domibus.connector.client.ui.service.ConnectorClientServiceClientException;
import eu.domibus.connector.client.ui.service.VaadingConnectorClientUIServiceClient;

@Component
@UIScope
@Route(value = SendMessage.ROUTE, layout= Messages.class)
public class SendMessage  extends VerticalLayout implements HasUrlParameter<Long>,AfterNavigationObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String ROUTE = "sendMessage";

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SendMessage.class);

	private VaadingConnectorClientUIServiceClient messageService;
	private Messages messagesView;
	
	private DynamicMessageForm messageForm = new DynamicMessageForm();
	private VerticalLayout messageFilesArea = new VerticalLayout();
	
	boolean filesEnabled = false;
	boolean saveEnabled = true;
	Button saveBtn;
	Button uploadFileButton;
	Button submitMessageButton;
	
	Div resultArea;
	Label resultLabel;
	
	public SendMessage(@Autowired Messages messagesView, @Autowired VaadingConnectorClientUIServiceClient messageService) {
		this.messagesView = messagesView;
		this.messageService = messageService;
		
		this.messagesView.setSendMessageView(this);
		
		VerticalLayout messageDetailsArea = new VerticalLayout(); 
		messageForm.getStyle().set("margin-top","25px");

		messageDetailsArea.add(messageForm);
		messageForm.setEnabled(true);
		messageDetailsArea.setWidth("500px");
		add(messageDetailsArea);
		
		saveBtn = new Button(new Icon(VaadinIcon.EDIT));
		saveBtn.setText("Save Message");
		saveBtn.addClickListener(e -> {
			BinderValidationStatus<DomibusConnectorClientMessage> validationStatus = messageForm.getBinder().validate();
			if(validationStatus.isOk()) {
				DomibusConnectorClientMessage msg = this.messageService.saveMessage(messageForm.getConnectorClientMessage());
				LumoLabel result = new LumoLabel();
				if(msg!=null) {
					messageForm.setConnectorClientMessage(msg);
					result.setText("Message successfully saved!");
					result.getStyle().set("color", "green");
				}else {
					result.setText("Save message failed!");
					result.getStyle().set("color", "red");
				}
				loadPreparedMessage(messageForm.getConnectorClientMessage().getId(), result);
			}
		});
		saveBtn.setEnabled(saveEnabled);
		
		uploadFileButton = new Button(new Icon(VaadinIcon.UPLOAD));
		uploadFileButton.setText("Add File to message");
		uploadFileButton.addClickListener(e -> {
			UploadMessageFileDialog uploadFileDialog = new UploadMessageFileDialog();
			Button uploadFile = new Button(new Icon(VaadinIcon.UPLOAD));
			uploadFile.setText("Add File to message");
			uploadFile.addClickListener(e1 -> {
				if(!StringUtils.isEmpty(uploadFileDialog.getFileName()) && 
						uploadFileDialog.getFileType().getValue()!=null && 
						uploadFileDialog.getFileContents()!=null && uploadFileDialog.getFileContents().length > 0) {
					String nok = checkFileValid(uploadFileDialog.getFileName(), uploadFileDialog.getFileType().getValue());
					LumoLabel resultLabel = new LumoLabel();
					if(nok==null) {
					DomibusConnectorClientMessageFile messageFile = new DomibusConnectorClientMessageFile(
							uploadFileDialog.getFileName(), uploadFileDialog.getFileType().getValue(), uploadFileDialog.getFileContents());
					messageFile.setStorageLocation(messageForm.getConnectorClientMessage().getStorageInfo());
					boolean result = this.messageService.uploadFileToMessage(messageFile);
					if(!result) {
						resultLabel.setText("Add file to message failed!");
						resultLabel.getStyle().set("color", "red");
//						uploadFileDialog.setErrorText("Upload file to message failed!");
					}else {
						resultLabel.setText("File successfully added to message");
						resultLabel.getStyle().set("color", "green");
					}
					}else {
						resultLabel.setText(nok);
						resultLabel.getStyle().set("color", "red");
					}
					uploadFileDialog.close();
					loadPreparedMessage(messageForm.getConnectorClientMessage().getId(), resultLabel);
					
				}
			});
			uploadFileDialog.add(uploadFile);
			uploadFileDialog.open();
		});
		uploadFileButton.setEnabled(filesEnabled);
		
		submitMessageButton = new Button(new Icon(VaadinIcon.CLOUD_UPLOAD_O));
		submitMessageButton.setText("Submit Message");
		submitMessageButton.addClickListener(e -> {
			if(validateMessageForm()) {
			LumoLabel resultLabel = new LumoLabel();
			if(!validateMessageForSumission()) {
				resultLabel.setText("For message submission a BUSINESS_CONTENT and BUSINESS_DOCUMENT must be present!");
				resultLabel.getStyle().set("color", "red");
			}else {
				try {
					DomibusConnectorClientMessage msg = this.messageService.saveMessage(messageForm.getConnectorClientMessage());
					this.messageService.submitStoredMessage(msg);
					resultLabel.setText("Message successfully submitted!");
					resultLabel.getStyle().set("color", "green");
				} catch (ConnectorClientServiceClientException e1) {
					resultLabel.setText("Exception thrown at connector client: "+e1.getMessage());
					resultLabel.getStyle().set("color", "red");
				}
				
			}
			loadPreparedMessage(messageForm.getConnectorClientMessage().getId(), resultLabel);
			}
		});
		submitMessageButton.setEnabled(filesEnabled && validateMessageForm());
		
		HorizontalLayout buttons = new HorizontalLayout(
				saveBtn, uploadFileButton, submitMessageButton
			    );
		buttons.setWidth("100vw");
		add(buttons);
		
		resultArea = new Div();
		
//		resultLabel = new LumoLabel();
//		resultArea.add(resultLabel);
		
		add(resultArea);
		
		add(messageFilesArea);
		
		setSizeFull();
		
	}
	
	private boolean validateMessageForm() {
		BinderValidationStatus<DomibusConnectorClientMessage> validationStatus = messageForm.getBinder().validate();
		return validationStatus.isOk();
	}
	
	private String checkFileValid(String fileName, DomibusConnectorClientMessageFileType fileType) {
		if(messageForm.getConnectorClientMessage().getFiles()!=null && messageForm.getConnectorClientMessage().getFiles().getFiles()!=null) {
			Iterator<DomibusConnectorClientMessageFile> fileIterator = messageForm.getConnectorClientMessage().getFiles().getFiles().iterator();
			
			while(fileIterator.hasNext()) {
				DomibusConnectorClientMessageFile file = fileIterator.next();
				if(file.getFileName().equals(fileName)) {
					return "File with that name already part of the message!";
				}
				switch(fileType) {
				case BUSINESS_CONTENT:if(file.getFileType().equals(fileType))return "BUSINESS_CONTENT already part of the message! Must not be more than one!";
				case BUSINESS_DOCUMENT:if(file.getFileType().equals(fileType))return "BUSINESS_DOCUMENT already part of the message! Must not be more than one!";
				case DETACHED_SIGNATURE:if(file.getFileType().equals(fileType))return "DETACHED_SIGNATURE already part of the message! Must not be more than one!";
				default:
				}
			}
			
		}
		return null;
	}
	
	private Button getDeleteFileLink(DomibusConnectorClientMessageFile file) {
		Button deleteFileButton = new Button(new Icon(VaadinIcon.ERASER));
		deleteFileButton.setEnabled(saveEnabled);
		deleteFileButton.addClickListener(e -> {
			Dialog deleteMessageDialog = new Dialog();
			
			Div headerContent = new Div();
			Label header = new Label("Delete file from message");
			header.getStyle().set("font-weight", "bold");
			header.getStyle().set("font-style", "italic");
			headerContent.getStyle().set("text-align", "center");
			headerContent.getStyle().set("padding", "10px");
			headerContent.add(header);
			deleteMessageDialog.add(headerContent);

			Div labelContent = new Div();
			LumoLabel label = new LumoLabel("Are you sure you want to delete this file from the message? Storage file is deleted as well!");
			
			labelContent.add(label);
			deleteMessageDialog.add(labelContent);
			
			Button delButton = new Button("Delete File");
			delButton.addClickListener(e1 -> {
				boolean result = this.messageService.deleteFileFromMessage(file);
				LumoLabel resultLabel = new LumoLabel();
				if(result) {
					resultLabel.setText("File "+file.getFileName()+" deleted successfully");
					resultLabel.getStyle().set("color", "green");
				}else {
					resultLabel.setText("Delete file "+file.getFileName()+" failed!");
					resultLabel.getStyle().set("color", "red");
				}
				deleteMessageDialog.close();
				loadPreparedMessage(messageForm.getConnectorClientMessage().getId(), resultLabel);
			});
			deleteMessageDialog.add(delButton);
			deleteMessageDialog.open();
			
		});
		return deleteFileButton;
	}
	
	private boolean validateMessageForSumission() {
		boolean businessDocumentFound = false;
		boolean businessContentFound = false;
		Iterator<DomibusConnectorClientMessageFile> it = messageForm.getConnectorClientMessage().getFiles().getFiles().iterator();
		while(it.hasNext()) {
			DomibusConnectorClientMessageFile file = it.next();
			if(file.getFileType().equals(DomibusConnectorClientMessageFileType.BUSINESS_CONTENT))
				businessContentFound = true;
			if(file.getFileType().equals(DomibusConnectorClientMessageFileType.BUSINESS_DOCUMENT))
				businessDocumentFound = true;
		}
		return businessContentFound && businessDocumentFound;
	}

	public void loadPreparedMessage(Long connectorMessageId, LumoLabel result) {
		DomibusConnectorClientMessage messageByConnectorId = null;
		try {
			messageByConnectorId = messageService.getMessageById(connectorMessageId);
		} catch (ConnectorClientServiceClientException e) {
			Dialog diag = messagesView.getErrorDialog("Error loading prepared Message from connector client", e.getMessage());
			Button okButton = new Button("OK");
			okButton.addClickListener(event -> {
				messagesView.showMessagesList();
				diag.close();
			});
			
			diag.add(okButton);
			
			diag.open();
			return;
			
		}
		messageForm.setConnectorClientMessage(messageByConnectorId);

		filesEnabled = messageForm.getConnectorClientMessage()!=null && 
				messageForm.getConnectorClientMessage().getStorageInfo()!=null && 
				!messageForm.getConnectorClientMessage().getStorageInfo().isEmpty() && 
				messageForm.getConnectorClientMessage().getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED.name());
		
		saveEnabled = messageForm.getConnectorClientMessage().getMessageStatus().equals("PREPARED");
		
		saveBtn.setEnabled(saveEnabled);
		uploadFileButton.setEnabled(filesEnabled && saveEnabled);
		submitMessageButton.setEnabled(filesEnabled && saveEnabled);
		
		buildMessageFilesArea(messageByConnectorId);

		if(result !=null) {
			resultArea.removeAll();
			resultArea.add(result);
			resultArea.setVisible(true);
		}else {
			resultArea.removeAll();
			resultArea.setVisible(false);
		}

	}
	
	private void buildMessageFilesArea(DomibusConnectorClientMessage messageByConnectorId) {
		
		messageFilesArea.removeAll();
		
		Div files = new Div();
		files.setWidth("100vw");
		LumoLabel filesLabel = new LumoLabel();
		filesLabel.setText("Files:");
		filesLabel.getStyle().set("font-size", "20px");
		files.add(filesLabel);
		
		messageFilesArea.add(files);
		
		Div details = new Div();
		details.setWidth("100vw");
		
		if(filesEnabled) {
			
			Grid<DomibusConnectorClientMessageFile> grid = new Grid<>();

			grid.setItems(messageByConnectorId.getFiles().getFiles());

			grid.addComponentColumn(domibusConnectorClientMessageFile -> getDeleteFileLink(domibusConnectorClientMessageFile)).setHeader("Delete").setWidth("50px");
			grid.addComponentColumn(domibusConnectorClientMessageFile -> createDownloadButton(filesEnabled,domibusConnectorClientMessageFile.getFileName(),messageByConnectorId.getStorageInfo())).setHeader("Filename").setWidth("500px");
			grid.addColumn(DomibusConnectorClientMessageFile::getFileType).setHeader("Filetype").setWidth("450px");

			grid.setWidth("1000px");
			grid.setMultiSort(true);

			for(Column<DomibusConnectorClientMessageFile> col : grid.getColumns()) {
				col.setSortable(true);
				col.setResizable(true);
			}

			details.add(grid);

		}
		messageFilesArea.add(details);
		
		messageFilesArea.setWidth("100vw");
		messageFilesArea.setVisible(filesEnabled);
	}
	
	private Anchor createDownloadButton(boolean enabled, String fileName, String storageLocation) {
		Label button = new Label(fileName);
		final StreamResource resource = new StreamResource(fileName,
				() -> new ByteArrayInputStream(messageService.loadFileContentFromStorageLocation(storageLocation, fileName)));

		Anchor downloadAnchor = new Anchor();
		if (enabled) {
			downloadAnchor.setHref(resource);
		}else {
			downloadAnchor.removeHref();
		}
		downloadAnchor.getElement().setAttribute("download", true);
		downloadAnchor.setTarget("_blank");
		downloadAnchor.setTitle(fileName);
		downloadAnchor.add(button);

		return downloadAnchor;
	}
	
	private void clearSendMessage() {
		messageForm.setConnectorClientMessage(new DomibusConnectorClientMessage());
		
		resultArea.removeAll();
		resultArea.setVisible(false);
		
		messageFilesArea.removeAll();
		messageFilesArea.setVisible(false);
	}

	@Override
	public void afterNavigation(AfterNavigationEvent arg0) {
		if(this.messagesView.getMessagesListView()!=null)this.messagesView.getMessagesListView().setVisible(false);
		if(this.messagesView.getMessageDetailsView()!=null)this.messagesView.getMessageDetailsView().setVisible(false);
		this.setVisible(true);
	}

	@Override
	public void setParameter(BeforeEvent event
		    , @OptionalParameter Long parameter) {
		if(parameter!=null) {
	    	loadPreparedMessage(parameter, null);
	    }else {
	    	clearSendMessage();
	    }
	}

}
