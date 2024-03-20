package eu.domibus.connector.client.ui.view.messages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

public class ReplyToMessageDialog extends VerticalLayout {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Messages messagesView;
	private VaadingConnectorClientUIServiceClient messageService;
	private Dialog replyToMessageDialog;
	
	private Set<String> selectedFiles = new HashSet<String>();
	List<DomibusConnectorClientMessageFile> files = null;
	private DomibusConnectorClientMessage originalMessage;

	public ReplyToMessageDialog(Dialog replyToMessageDialog, DomibusConnectorClientMessage message, Messages messagesView, VaadingConnectorClientUIServiceClient messageService) {
		this.messagesView = messagesView;
		this.messageService = messageService;
		this.replyToMessageDialog = replyToMessageDialog;
		originalMessage = message;
		Div info = new Div();
		info.setWidth("100vw");
		LumoLabel infoLabel = new LumoLabel();
		infoLabel.setText("Creating a new message to be sent. Most values of the message will be filled with attributes from the original message.");
		infoLabel.getStyle().set("font-size", "20px");
		info.add(infoLabel);

		add(info);
		
		boolean filesEnabled = message.getStorageInfo()!=null && !message.getStorageInfo().isEmpty() && message.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED.name());

		if(filesEnabled) {
			Div files = new Div();
			files.setWidth("100vw");
			LumoLabel filesLabel = new LumoLabel();
			filesLabel.setText("Select files from the original message the reply message should contain as business attachment:");
			filesLabel.getStyle().set("font-size", "20px");
			files.add(filesLabel);

			add(files);

			Div details = new Div();
			details.setWidth("100vw");


			Grid<DomibusConnectorClientMessageFile> grid = new Grid<>();

			grid.setItems(message.getFiles().getFiles());

			grid.addComponentColumn(domibusConnectorClientMessageFile -> createSelectionCheckbox(domibusConnectorClientMessageFile.getFileName())).setHeader("Filename").setWidth("30px");
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
		
		Button createReply = new Button("Create reply");
		createReply.addClickListener(e -> {
			createReply();
		});
		
		add(createReply);
	}
	
	private Checkbox createSelectionCheckbox(String fileName) {
		Checkbox selection = new Checkbox();
		
		selection.addValueChangeListener(e -> {
			if(e.getValue()) {
				selectedFiles.add(fileName);
			}else {
				selectedFiles.remove(fileName);
			}
		});
		
		return selection;
	}
	
	private void createReply() {
		DomibusConnectorClientMessage replyMessage = createNewReplyMessage();
		
		if(!selectedFiles.isEmpty()) {
			files = new ArrayList<DomibusConnectorClientMessageFile>();
			selectedFiles.forEach(file -> {
				byte[] fileContent = this.messageService.loadFileContentFromStorageLocation(originalMessage.getStorageInfo(), file);
				DomibusConnectorClientMessageFile att = new DomibusConnectorClientMessageFile(file, DomibusConnectorClientMessageFileType.BUSINESS_ATTACHMENT, fileContent);
				files.add(att);
			});
		}
		
		replyMessage.getFiles().setFiles(files);
		replyMessage = messageService.saveMessage(replyMessage);
		
		messagesView.showSendMessage(replyMessage.getId());
		
		replyToMessageDialog.close();
	}
	
	private DomibusConnectorClientMessage createNewReplyMessage() {
		DomibusConnectorClientMessage replyMessage = new DomibusConnectorClientMessage();
		
		replyMessage.setConversationId(originalMessage.getConversationId());
		
		replyMessage.setFinalRecipient(originalMessage.getOriginalSender());
		replyMessage.setOriginalSender(originalMessage.getFinalRecipient());
		
		replyMessage.setFromPartyId(originalMessage.getToPartyId());
		replyMessage.setFromPartyRole(originalMessage.getToPartyRole());
		replyMessage.setFromPartyType(originalMessage.getToPartyType());
		
		replyMessage.setToPartyId(originalMessage.getFromPartyId());
		replyMessage.setToPartyRole(originalMessage.getFromPartyRole());
		replyMessage.setToPartyType(originalMessage.getFromPartyType());
		
		replyMessage.setService(originalMessage.getService());
		replyMessage.setServiceType(originalMessage.getServiceType());

		return replyMessage;
	}

}
