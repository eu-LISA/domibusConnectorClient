package eu.domibus.connector.client.ui.view.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.client.ui.component.LumoLabel;
import eu.domibus.connector.client.ui.service.VaadingConnectorClientUIServiceClient;

public class ResendMessageDialog extends VerticalLayout {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Messages messagesView;
	private VaadingConnectorClientUIServiceClient messageService;
	private Dialog resendMessageDialog;
	
	private Map<String, DomibusConnectorClientMessageFileType> selectedFiles = new HashMap<String, DomibusConnectorClientMessageFileType>();
	List<DomibusConnectorClientMessageFile> files = null;
	private DomibusConnectorClientMessage originalMessage;

	public ResendMessageDialog(Dialog resendMessageDialog, DomibusConnectorClientMessage message, Messages messagesView, VaadingConnectorClientUIServiceClient messageService) {
		this.messagesView = messagesView;
		this.messageService = messageService;
		this.resendMessageDialog = resendMessageDialog;
		originalMessage = message;
		Div info = new Div();
		info.setWidth("100vw");
		LumoLabel infoLabel = new LumoLabel();
		infoLabel.setText("Creating a new message to be sent. All values of the message except unique message ids will be filled with attributes from the original message.");
		infoLabel.getStyle().set("font-size", "20px");
		info.add(infoLabel);

		add(info);
		
		boolean filesEnabled = message.getStorageInfo()!=null && !message.getStorageInfo().isEmpty() && message.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED.name());

		if(filesEnabled) {
			Div files = new Div();
			files.setWidth("100vw");
			LumoLabel filesLabel = new LumoLabel();
			filesLabel.setText("Select files from the original message to be sent with the new message:");
			filesLabel.getStyle().set("font-size", "20px");
			files.add(filesLabel);

			add(files);

			Div details = new Div();
			details.setWidth("100vw");


			Grid<DomibusConnectorClientMessageFile> grid = new Grid<>();

			grid.setItems(message.getFiles().getFiles());

			grid.addComponentColumn(domibusConnectorClientMessageFile -> createSelectionCheckbox(domibusConnectorClientMessageFile)).setHeader("Filename").setWidth("30px");
			grid.addColumn(DomibusConnectorClientMessageFile::getFileName).setHeader("Filename").setWidth("500px");
			grid.addColumn(DomibusConnectorClientMessageFile::getFileType).setHeader("Filetype").setWidth("450px");

			grid.setWidth("1000px");
			//			grid.setHeight("210px");
			grid.setMultiSort(true);

			for(Column<DomibusConnectorClientMessageFile> col : grid.getColumns()) {
				col.setSortable(true);
				col.setResizable(true);
			}

			details.add(grid);
			
			add(details);

		}
		
		Button createReply = new Button("Create message");
		createReply.addClickListener(e -> {
			createNewMessage();
		});
		
		add(createReply);
	}
	
	private Checkbox createSelectionCheckbox(DomibusConnectorClientMessageFile file) {
		Checkbox selection = new Checkbox();
		
		selection.addValueChangeListener(e -> {
			if(e.getValue()) {
				selectedFiles.put(file.getFileName(), file.getFileType());
			}else {
				selectedFiles.remove(file.getFileName());
			}
		});
		
		return selection;
	}
	
	private void createNewMessage() {
		DomibusConnectorClientMessage replyMessage = createNewReplyMessage();
		
		if(!selectedFiles.isEmpty()) {
			files = new ArrayList<DomibusConnectorClientMessageFile>();
			selectedFiles.keySet().forEach(fileName -> {
				byte[] fileContent = this.messageService.loadFileContentFromStorageLocation(originalMessage.getStorageInfo(), fileName);
				DomibusConnectorClientMessageFile att = new DomibusConnectorClientMessageFile(fileName, selectedFiles.get(fileName), fileContent);
				files.add(att);
			});
		}
		
		replyMessage.getFiles().setFiles(files);
		replyMessage = messageService.saveMessage(replyMessage);
		
		messagesView.showSendMessage(replyMessage.getId());
		
		resendMessageDialog.close();
	}
	
	private DomibusConnectorClientMessage createNewReplyMessage() {
		DomibusConnectorClientMessage replyMessage = new DomibusConnectorClientMessage();
		
		replyMessage.setConversationId(originalMessage.getConversationId());
		
		replyMessage.setFinalRecipient(originalMessage.getFinalRecipient());
		replyMessage.setOriginalSender(originalMessage.getOriginalSender());
		
		replyMessage.setFromPartyId(originalMessage.getFromPartyId());
		replyMessage.setFromPartyRole(originalMessage.getFromPartyRole());
		replyMessage.setFromPartyType(originalMessage.getFromPartyType());
		
		replyMessage.setToPartyId(originalMessage.getToPartyId());
		replyMessage.setToPartyRole(originalMessage.getToPartyRole());
		replyMessage.setToPartyType(originalMessage.getToPartyType());
		
		replyMessage.setService(originalMessage.getService());
		replyMessage.setServiceType(originalMessage.getServiceType());
		replyMessage.setAction(originalMessage.getAction());
		
		return replyMessage;
	}

}
