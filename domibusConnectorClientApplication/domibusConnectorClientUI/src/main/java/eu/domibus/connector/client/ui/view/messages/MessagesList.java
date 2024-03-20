package eu.domibus.connector.client.ui.view.messages;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import eu.domibus.connector.client.ui.component.LumoLabel;
import eu.domibus.connector.client.ui.service.ConnectorClientServiceClientException;
import eu.domibus.connector.client.ui.service.VaadingConnectorClientUIServiceClient;

@Component
@UIScope
@Route(value = "messagesList", layout= Messages.class)
public class MessagesList extends VerticalLayout implements AfterNavigationObserver {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Messages messagesView;
	private VaadingConnectorClientUIServiceClient messageService;
	
	private Grid<DomibusConnectorClientMessage> grid;
	private List<DomibusConnectorClientMessage> fullList = null;
	
	TextField searchEbmsIdText = new TextField();
	TextField searchBackendMessageIdText = new TextField();
	TextField searchConversationIdText = new TextField();
	
	DatePicker fromDate;
	DatePicker toDate;
	
	TextField messageStatusFilterText = new TextField();
	TextField fromPartyIdFilterText = new TextField();
	TextField toPartyIdFilterText = new TextField();
	TextField serviceFilterText = new TextField();

	TextField serviceTypeFilterText = new TextField();
	TextField actionFilterText = new TextField();
	
	public MessagesList(@Autowired Messages messagesView, @Autowired VaadingConnectorClientUIServiceClient messageService) {
		this.messagesView = messagesView;
		this.messageService = messageService;
		
		this.messagesView.setMessagesListView(this);
		
		fullList = messageService.getAllMessages().getMessages();
		
		grid = createMessageList();
		grid.setItems(fullList);
		
		VerticalLayout search = createSearchLayout();
		add(search);
		
		HorizontalLayout filtering = createFilterLayout();
		
//		HorizontalLayout downloadLayout = createDownloadLayout();
			
		VerticalLayout main = new VerticalLayout(filtering, grid
//				, downloadLayout
				);
		main.setAlignItems(Alignment.STRETCH);
		main.setHeight("700px");
		add(main);
		setHeight("100vh");
		setWidth("100vw");
		reloadFullList();
		
	}
	
//	private HorizontalLayout createDownloadLayout() {
//		Div downloadExcel = new Div();
//		
//		Button download = new Button();
//		download.setIcon(new Image("frontend/images/xls.png", "XLS"));
//		
//		download.addClickListener(e -> {
//		
//			Element file = new Element("object");
//			Element dummy = new Element("object");
//			
//			Input oName = new Input();
//			
//			String name = "MessagesList.xls";
//			
//			StreamResource resource = new StreamResource(name,() -> getMessagesListExcel());
//			
//			resource.setContentType("application/xls");
//			
//			file.setAttribute("data", resource);
//			
//			Anchor link = null;
//			link = new Anchor(file.getAttribute("data"), "Download Document");
//			
//			UI.getCurrent().getElement().appendChild(oName.getElement(), file,
//					dummy);
//			oName.setVisible(false);
//			file.setVisible(false);
//			this.getUI().get().getPage().executeJavaScript("window.open('"+link.getHref()+"');");
//		});
//		
//		downloadExcel.add(download);
//		
//		HorizontalLayout downloadLayout = new HorizontalLayout(
//				downloadExcel
//			    );
//		downloadLayout.setWidth("100vw");
//		
//		return downloadLayout;
//	}
	
//	private InputStream getMessagesListExcel() {
//		return messageService.generateExcel(fullList);
//	}
	
	private VerticalLayout createSearchLayout() {
		VerticalLayout searchLayout = new VerticalLayout();
		
		HorizontalLayout ebmsIdSearch = new HorizontalLayout();
		
		searchEbmsIdText.setPlaceholder("Search by EBMS Message ID");
		searchEbmsIdText.setWidth("300px");
		ebmsIdSearch.add(searchEbmsIdText);
		
		Button searchEbmsIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
		searchEbmsIdBtn.addClickListener(e -> searchByEbmsId());
		ebmsIdSearch.add(searchEbmsIdBtn);
		
		searchLayout.add(ebmsIdSearch);
		
		HorizontalLayout backendMessageIdSearch = new HorizontalLayout();
		
		searchBackendMessageIdText.setPlaceholder("Search by Backend Message ID");
		searchBackendMessageIdText.setWidth("300px");
		backendMessageIdSearch.add(searchBackendMessageIdText);
		
		Button searchBackendMessageIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
		searchBackendMessageIdBtn.addClickListener(e -> searchByBackendMessageId());
		backendMessageIdSearch.add(searchBackendMessageIdBtn);
		
		searchLayout.add(backendMessageIdSearch);
		
		HorizontalLayout conversationIdSearch = new HorizontalLayout();
		
		searchConversationIdText.setPlaceholder("Search by Conversation ID");
		searchConversationIdText.setWidth("300px");
		conversationIdSearch.add(searchConversationIdText);
		
		Button searchConversationIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
		searchConversationIdBtn.addClickListener(e -> searchByConversationId());
		conversationIdSearch.add(searchConversationIdBtn);
		
		searchLayout.add(conversationIdSearch);
		
		HorizontalLayout dateSearch = new HorizontalLayout();
		
		fromDate = new DatePicker();
		fromDate.setLabel("From Date");
		fromDate.setErrorMessage("From Date invalid!");
		dateSearch.add(fromDate);
		
		toDate = new DatePicker();
		toDate.setLabel("To Date");
		toDate.setErrorMessage("To Date invalid!");
		dateSearch.add(toDate);
		
		Button searchPeriodBtn = new Button(new Icon(VaadinIcon.SEARCH));
		searchPeriodBtn.addClickListener(e -> searchByPeriod());
		dateSearch.add(searchPeriodBtn);
		
		dateSearch.setAlignItems(Alignment.END);
		
		searchLayout.add(dateSearch);

		Button resetSearch = new Button(
				new Icon(VaadinIcon.CLOSE_CIRCLE));
		resetSearch.setText("Reset Search");
		resetSearch.addClickListener(e -> {
			searchEbmsIdText.clear();
			searchBackendMessageIdText.clear();
			searchConversationIdText.clear();
			fromDate.clear();
			toDate.clear();
			reloadFullList();
			});
		
		searchLayout.add(resetSearch);
		
		return searchLayout;
	}
	
	private void searchByBackendMessageId() {
		searchEbmsIdText.clear();
		searchConversationIdText.clear();
		fromDate.clear();
		toDate.clear();
		DomibusConnectorClientMessage message = null;
		try {
			message = messageService.getMessageByBackendMessageId(searchBackendMessageIdText.getValue());
		} catch (ConnectorClientServiceClientException e) {
			openErrorDialog(e.getMessage());
			return;
		}
		messagesView.showMessageDetails(message.getId());
	}

	private void searchByEbmsId() {
		searchBackendMessageIdText.clear();
		searchConversationIdText.clear();
		fromDate.clear();
		toDate.clear();
		DomibusConnectorClientMessage message = null;
		try {
			message = messageService.getMessageByEbmsId(searchEbmsIdText.getValue());
		} catch (ConnectorClientServiceClientException e) {
			openErrorDialog(e.getMessage());
			return;
		}
		messagesView.showMessageDetails(message.getId());
	}

	private void searchByPeriod() {
		Date fromDate = asDate(this.fromDate.getValue());
		Date toDate = asDate(this.toDate.getValue());
		searchEbmsIdText.clear();
		searchBackendMessageIdText.clear();
		searchConversationIdText.clear();
		
		if(fromDate==null) {
			fromDate = new Date(1);
		}
		if(toDate==null) {
			toDate = new Date(System.currentTimeMillis());
		}else {
			toDate = new Date(toDate.getTime() + TimeUnit.DAYS.toMillis( 1 ));
		}
		DomibusConnectorClientMessageList fullList = null;
		try {
			fullList = messageService.getMessagesByPeriod(fromDate, toDate);
		} catch (ConnectorClientServiceClientException e) {
			openErrorDialog(e.getMessage());
			return;
		}
		grid.setItems(fullList.getMessages());
	}
	
	private void searchByConversationId() {
		searchEbmsIdText.clear();
		searchBackendMessageIdText.clear();
		fromDate.clear();
		toDate.clear();
		DomibusConnectorClientMessageList fullList = null;
		try {
			fullList = messageService.getMessagesByConversationId(searchConversationIdText.getValue());
		} catch (ConnectorClientServiceClientException e) {
			openErrorDialog(e.getMessage());
			return;
		}
		grid.setItems(fullList.getMessages());
	}
	
	private void openErrorDialog(String message) {
		Dialog diag = messagesView.getErrorDialog("Error retrieve Message from connector client", message);
		Button okButton = new Button("OK");
		okButton.addClickListener(event -> {
//			messagesView.showMessagesList();
			diag.close();
		});
		
		diag.add(okButton);
		
		diag.open();
	}

	public static Date asDate(LocalDate localDate) {
	    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	  }
	
	private HorizontalLayout createFilterLayout() {
		
		messageStatusFilterText.setPlaceholder("Filter by MessageStatus");
		messageStatusFilterText.setWidth("180px");
		messageStatusFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		messageStatusFilterText.addValueChangeListener(e -> filter());
		
		fromPartyIdFilterText.setPlaceholder("Filter by From Party ID");
		fromPartyIdFilterText.setWidth("180px");
		fromPartyIdFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		fromPartyIdFilterText.addValueChangeListener(e -> filter());

		
		toPartyIdFilterText.setPlaceholder("Filter by To Party ID");
		toPartyIdFilterText.setWidth("180px");
		toPartyIdFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		toPartyIdFilterText.addValueChangeListener(e -> filter());
		
		
		serviceFilterText.setPlaceholder("Filter by Service");
		serviceFilterText.setWidth("180px");
		serviceFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		serviceFilterText.addValueChangeListener(e -> filter());

		serviceTypeFilterText.setPlaceholder("Filter by Service Type");
		serviceTypeFilterText.setWidth("180px");
		serviceTypeFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		serviceTypeFilterText.addValueChangeListener(e -> filter());
		
		actionFilterText.setPlaceholder("Filter by Action");
		actionFilterText.setWidth("180px");
		actionFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		actionFilterText.addValueChangeListener(e -> filter());
		
		Button clearAllFilterTextBtn = new Button(
				new Icon(VaadinIcon.CLOSE_CIRCLE));
		clearAllFilterTextBtn.setText("Clear Filter");
		clearAllFilterTextBtn.addClickListener(e -> {
			messageStatusFilterText.clear();
			fromPartyIdFilterText.clear();
			toPartyIdFilterText.clear();
			serviceFilterText.clear();
			actionFilterText.clear();
			});
		
		Button refreshListBtn = new Button(new Icon(VaadinIcon.REFRESH));
		refreshListBtn.setText("RefreshList");
		refreshListBtn.addClickListener(e -> {
			reloadList();
		});
		
		HorizontalLayout filtering = new HorizontalLayout(
				messageStatusFilterText,
				fromPartyIdFilterText,
				toPartyIdFilterText,
				serviceFilterText,
				actionFilterText,
				clearAllFilterTextBtn,
				refreshListBtn
			    );
		filtering.setWidth("100vw");
		
		return filtering;
	}
	
	private void filter() {
		LinkedList<DomibusConnectorClientMessage> target = new LinkedList<DomibusConnectorClientMessage>();
		for(DomibusConnectorClientMessage msg : fullList) {
			if((messageStatusFilterText.getValue().isEmpty() || msg.getMessageStatus()!=null && msg.getMessageStatus().toUpperCase().contains(messageStatusFilterText.getValue().toUpperCase()))
				&& (fromPartyIdFilterText.getValue().isEmpty() || msg.getFromPartyId()!=null && msg.getFromPartyId().toUpperCase().contains(fromPartyIdFilterText.getValue().toUpperCase()))
				&& (toPartyIdFilterText.getValue().isEmpty() || msg.getToPartyId()!=null && msg.getToPartyId().toUpperCase().contains(toPartyIdFilterText.getValue().toUpperCase()))
				&& (serviceFilterText.getValue().isEmpty() || msg.getService()!=null && msg.getService().toUpperCase().contains(serviceFilterText.getValue().toUpperCase()))
				&& (serviceTypeFilterText.getValue().isEmpty() || msg.getServiceType()!=null && msg.getServiceType().toUpperCase().contains(serviceTypeFilterText.getValue().toUpperCase()))
				&& (actionFilterText.getValue().isEmpty() || msg.getAction()!=null && msg.getAction().toUpperCase().contains(actionFilterText.getValue().toUpperCase()))
				) {
				target.addLast(msg);
			}
		}
		
		grid.setItems(target);
	}
	
	public Grid<DomibusConnectorClientMessage> createMessageList() {
		Grid<DomibusConnectorClientMessage> grid = new Grid<>();
		
		grid.addComponentColumn(domibusConnectorClientMessage -> getDetailsLink(domibusConnectorClientMessage.getId())).setHeader("Details").setWidth("50px");
		grid.addComponentColumn(domibusConnectorClientMessage -> getDeleteMessageLink(domibusConnectorClientMessage.getId())).setHeader("Delete").setWidth("50px");
		grid.addComponentColumn(domibusConnectorClientMessage -> getEditMessageLink(domibusConnectorClientMessage)).setHeader("Edit").setWidth("50px");
		grid.addColumn(DomibusConnectorClientMessage::getEbmsMessageId).setHeader("ebmsMessageID").setWidth("150px");
		grid.addColumn(DomibusConnectorClientMessage::getBackendMessageId).setHeader("backendMessageID").setWidth("150px");
		grid.addColumn(DomibusConnectorClientMessage::getConversationId).setHeader("conversationID").setWidth("150px");
		grid.addColumn(DomibusConnectorClientMessage::getFromPartyId).setHeader("From Party ID").setWidth("100px");
		grid.addColumn(DomibusConnectorClientMessage::getToPartyId).setHeader("To Party ID").setWidth("100px");
		grid.addColumn(DomibusConnectorClientMessage::getService).setHeader("Service").setWidth("100px");
		grid.addColumn(DomibusConnectorClientMessage::getServiceType).setHeader("Service Type").setWidth("100px");
		grid.addColumn(DomibusConnectorClientMessage::getAction).setHeader("Action").setWidth("100px");
		grid.addColumn(DomibusConnectorClientMessage::getCreated).setHeader("Created").setWidth("150px");
		grid.addColumn(DomibusConnectorClientMessage::getMessageStatus).setHeader("Message status").setWidth("100px");
		grid.addColumn(DomibusConnectorClientMessage::getLastConfirmationReceived).setHeader("last confirmation received").setWidth("150px");
		grid.addColumn(DomibusConnectorClientMessage::getStorageInfo).setHeader("Storage info").setWidth("150px");
		grid.addColumn(DomibusConnectorClientMessage::getStorageStatus).setHeader("Storage status").setWidth("100px");
		grid.setWidth("2480px");
		grid.setHeight("700px");
		grid.setMultiSort(true);
		
		for(Column<DomibusConnectorClientMessage> col : grid.getColumns()) {
			col.setSortable(true);
			col.setResizable(true);
		}
		
		return grid;
	}
	
	public Button getDetailsLink(Long l) {
		Button getDetails = new Button(new Icon(VaadinIcon.SEARCH));
		getDetails.addClickListener(e -> 
			this.messagesView.showMessageDetails(l)
			);
		return getDetails;
	}
	
	private Button getDeleteMessageLink(Long l) {
		Button deleteMessageButton = new Button(new Icon(VaadinIcon.ERASER));
		deleteMessageButton.addClickListener(e -> {
			Dialog deleteMessageDialog = this.messagesView.getDeleteMessageDialog();
			Button delButton = new Button("Delete Message");
			delButton.addClickListener(e1 -> {
				try {
					this.messageService.deleteMessageById(l);
				} catch (ConnectorClientServiceClientException e2) {
					Dialog errorDiag = new Dialog();
					LumoLabel resultLabel = new LumoLabel();
					resultLabel.setText("Delete message failed: "+e2.getMessage());
					resultLabel.getStyle().set("color", "red");
					errorDiag.add(resultLabel);
					errorDiag.open();
				}
				deleteMessageDialog.close();
				reloadList();
			});
			deleteMessageDialog.add(delButton);
			deleteMessageDialog.open();
			
		});
		return deleteMessageButton;
	}
	
	private Button getEditMessageLink(DomibusConnectorClientMessage domibusConnectorClientMessage) {
		Button editMessageButton = new Button(new Icon(VaadinIcon.EDIT));
		editMessageButton.addClickListener(e -> {
			this.messagesView.showSendMessage(domibusConnectorClientMessage.getId());
		});
		editMessageButton.setEnabled(domibusConnectorClientMessage.getMessageStatus()!=null && domibusConnectorClientMessage.getMessageStatus().equalsIgnoreCase("PREPARED"));
		return editMessageButton;
	}
	
	private void reloadList() {
		if(!searchEbmsIdText.isEmpty()) {
			searchByEbmsId();
		}else if (!searchBackendMessageIdText.isEmpty()) {
			searchByBackendMessageId();
		}else if (!searchConversationIdText.isEmpty()) {
			searchByConversationId();
		}else if (!fromDate.isEmpty() || !toDate.isEmpty()) {
			searchByPeriod();
		}else
		reloadFullList();
	}
	
	public void reloadFullList() {
		grid.setItems(messageService.getAllMessages().getMessages());
	}
	
	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		reloadFullList();
		if(this.messagesView.getMessageDetailsView()!=null)this.messagesView.getMessageDetailsView().setVisible(false);
		if(this.messagesView.getSendMessageView()!=null)this.messagesView.getSendMessageView().setVisible(false);
		this.setVisible(true);
	}
	
}
