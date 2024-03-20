package eu.domibus.connector.client.ui.view.messages;

import java.io.ByteArrayInputStream;

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
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientConfirmation;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.client.ui.component.LumoLabel;
import eu.domibus.connector.client.ui.form.DomibusConnectorClientMessageForm;
import eu.domibus.connector.client.ui.service.ConnectorClientServiceClientException;
import eu.domibus.connector.client.ui.service.VaadingConnectorClientUIServiceClient;

@Component
@Route(value = MessageDetails.ROUTE, layout= Messages.class)
@UIScope
public class MessageDetails extends VerticalLayout implements HasUrlParameter<Long>,AfterNavigationObserver {
	
	public static final String ROUTE = "messageDetails";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	Messages messagesView;
	
	VaadingConnectorClientUIServiceClient messageService;

	private DomibusConnectorClientMessageForm messageForm = new DomibusConnectorClientMessageForm();
	private VerticalLayout messageEvidencesArea = new VerticalLayout(); 
	private VerticalLayout messageFilesArea = new VerticalLayout();
	
	Button replyToMessageButton;
	Button resendMessageButton;
	Button refreshButton;
	Button deleteMessageButton;
	
	Div resultArea;
	
	
	public MessageDetails(@Autowired Messages messagesView, @Autowired VaadingConnectorClientUIServiceClient messageService) {
		
		this.messagesView = messagesView;
		this.messageService = messageService;

		this.messagesView.setMessageDetailsView(this);
	
		refreshButton = new Button(new Icon(VaadinIcon.REFRESH));
		refreshButton.setText("Refresh");
		refreshButton.addClickListener(e -> {
			loadMessageDetails(messageForm.getConnectorClientMessage().getId(), null);
		});
		refreshButton.setEnabled(false);
		
		deleteMessageButton = new Button(new Icon(VaadinIcon.ERASER));
		deleteMessageButton.setText("Delete");
		deleteMessageButton.addClickListener(e -> {
			Dialog deleteMessageDialog = this.messagesView.getDeleteMessageDialog();
			Button delButton = new Button("Delete Message");
			delButton.addClickListener(e1 -> {
				try {
					this.messageService.deleteMessageById(messageForm.getConnectorClientMessage().getId());
				} catch (ConnectorClientServiceClientException e2) {
					LumoLabel resultLabel = new LumoLabel();
						resultLabel.setText("Delete message failed: "+e2.getMessage());
						resultLabel.getStyle().set("color", "red");
						loadMessageDetails(messageForm.getConnectorClientMessage().getId(), resultLabel);
					
				}
				deleteMessageDialog.close();
				clearMessageDetails();
				this.messagesView.showMessagesList();
			});
			deleteMessageDialog.add(delButton);
			deleteMessageDialog.open();
		});
		deleteMessageButton.setEnabled(false);

		replyToMessageButton = new Button(new Icon(VaadinIcon.PLUS));
		replyToMessageButton.setText("Send reply to Message");
		replyToMessageButton.addClickListener(e -> openReplyToMessageDialog());
		replyToMessageButton.setEnabled(false);
		
		resendMessageButton = new Button(new Icon(VaadinIcon.ARROW_FORWARD));
		resendMessageButton.setText("Resend Message");
		resendMessageButton.addClickListener(e -> openResendMessageDialog());
		resendMessageButton.setEnabled(false);
		
		HorizontalLayout buttons = new HorizontalLayout(
				refreshButton, deleteMessageButton, replyToMessageButton, resendMessageButton
				);
		buttons.setWidth("100vw");
		add(buttons);
		
		resultArea = new Div();
		
//		resultLabel = new LumoLabel();
//		resultArea.add(resultLabel);
		
		add(resultArea);
		
		VerticalLayout messageDetailsArea = new VerticalLayout(); 
		messageForm.getStyle().set("margin-top","25px");

		messageDetailsArea.add(messageForm);
		messageForm.setEnabled(true);
		messageDetailsArea.setWidth("500px");
		add(messageDetailsArea);

		add(messageFilesArea);

		add(messageEvidencesArea);

		setSizeFull();
	}


	private void openReplyToMessageDialog() {
		Dialog replyToMessageDialog = new Dialog();

		Div headerContent = new Div();
		Label header = new Label("Send reply to a message");
		header.getStyle().set("font-weight", "bold");
		header.getStyle().set("font-style", "italic");
		headerContent.getStyle().set("text-align", "center");
		headerContent.getStyle().set("padding", "10px");
		headerContent.add(header);
		replyToMessageDialog.add(headerContent);

		ReplyToMessageDialog view = new ReplyToMessageDialog(replyToMessageDialog, messageForm.getConnectorClientMessage(), messagesView, messageService);
		replyToMessageDialog.add(view);

		replyToMessageDialog.open();
	}
	
	private void openResendMessageDialog() {
		Dialog resendMessageDialog = new Dialog();

		Div headerContent = new Div();
		Label header = new Label("Resend message");
		header.getStyle().set("font-weight", "bold");
		header.getStyle().set("font-style", "italic");
		headerContent.getStyle().set("text-align", "center");
		headerContent.getStyle().set("padding", "10px");
		headerContent.add(header);
		resendMessageDialog.add(headerContent);

		ResendMessageDialog view = new ResendMessageDialog(resendMessageDialog, messageForm.getConnectorClientMessage(), messagesView, messageService);
		resendMessageDialog.add(view);

		resendMessageDialog.open();
	}


	public void loadMessageDetails(Long msgId, LumoLabel result) {
		
		if(msgId!=null) {
		DomibusConnectorClientMessage msg = null;
		try {
			msg = messageService.getMessageById(msgId);
		} catch (ConnectorClientServiceClientException e) {
			Dialog diag = messagesView.getErrorDialog("Error retrieve Message from connector client", e.getMessage());
			Button okButton = new Button("OK");
			okButton.addClickListener(event -> {
				messagesView.showMessagesList();
				diag.close();
			});
			
			diag.add(okButton);
			
			diag.open();
			return;
		}
		messageForm.setConnectorClientMessage(msg);

		buildMessageFilesArea(msg);

		buildMessageEvidencesArea(msg);
		
		refreshButton.setEnabled(true);
		deleteMessageButton.setEnabled(true);
		replyToMessageButton.setEnabled(messageForm.getConnectorClientMessage()!=null && messageForm.getConnectorClientMessage().getMessageStatus().equals("CONFIRMED"));
		resendMessageButton.setEnabled(true);
		
		if(result !=null) {
			resultArea.removeAll();
			resultArea.add(result);
			resultArea.setVisible(true);
		}else {
			resultArea.removeAll();
			resultArea.setVisible(false);
		}
		
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

		boolean filesEnabled = messageByConnectorId.getStorageInfo()!=null && !messageByConnectorId.getStorageInfo().isEmpty() && messageByConnectorId.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED.name());

		if(filesEnabled) {
			
			Grid<DomibusConnectorClientMessageFile> grid = new Grid<>();

			grid.setItems(messageByConnectorId.getFiles().getFiles());

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
		//		add(messageEvidencesArea);
		messageFilesArea.setVisible(true);
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

	private void buildMessageEvidencesArea(DomibusConnectorClientMessage messageByConnectorId) {
		if(!messageByConnectorId.getEvidences().isEmpty()) {
			messageEvidencesArea.removeAll();

			Div evidences = new Div();
			evidences.setWidth("100vw");
			LumoLabel evidencesLabel = new LumoLabel();
			evidencesLabel.setText("Evidences:");
			evidencesLabel.getStyle().set("font-size", "20px");
			evidences.add(evidencesLabel);

			messageEvidencesArea.add(evidences);

			Div details = new Div();
			details.setWidth("100vw");

			Grid<DomibusConnectorClientConfirmation> grid = new Grid<>();

			grid.setItems(messageByConnectorId.getEvidences());

			grid.addColumn(DomibusConnectorClientConfirmation::getConfirmationType).setHeader("Confirmation Type").setWidth("250px");
			grid.addColumn(DomibusConnectorClientConfirmation::getReceived).setHeader("Received").setWidth("300px");

			grid.setWidth("1000px");
			grid.setHeight("210px");
			grid.setMultiSort(true);

			for(Column<DomibusConnectorClientConfirmation> col : grid.getColumns()) {
				col.setSortable(true);
				col.setResizable(true);
			}

			details.add(grid);



			messageEvidencesArea.add(details);

			messageEvidencesArea.setWidth("100vw");
			messageEvidencesArea.setVisible(true);
		}

	}


	@Override
	  public void setParameter(BeforeEvent event
	    , @OptionalParameter Long parameter) {
	    if(parameter!=null) {
	    	loadMessageDetails(parameter, null);
	    }else {
	    	clearMessageDetails();
	    }
	  }
	
	private void clearMessageDetails() {
		messageForm.setConnectorClientMessage(new DomibusConnectorClientMessage());
		refreshButton.setEnabled(false);
		deleteMessageButton.setEnabled(false);
		replyToMessageButton.setEnabled(false);
		
		messageEvidencesArea.removeAll();
		messageEvidencesArea.setVisible(false);
		
		messageFilesArea.removeAll();
		messageFilesArea.setVisible(false);
	}


	@Override
	public void afterNavigation(AfterNavigationEvent arg0) {
		if(this.messagesView.getMessagesListView()!=null)this.messagesView.getMessagesListView().setVisible(false);
		if(this.messagesView.getSendMessageView()!=null)this.messagesView.getSendMessageView().setVisible(false);
		this.setVisible(true);
		
	}

}
