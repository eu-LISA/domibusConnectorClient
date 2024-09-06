/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.ui.view.messages;

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
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageList;
import eu.ecodex.connector.client.ui.ViewConstant;
import eu.ecodex.connector.client.ui.component.LumoLabel;
import eu.ecodex.connector.client.ui.service.ConnectorClientServiceClientException;
import eu.ecodex.connector.client.ui.service.VaadingConnectorClientUIServiceClient;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The MessagesList class represents a component that manages and displays a list of messages in the
 * Domibus connector client UI.
 */
@Component
@UIScope
@Route(value = "messagesList", layout = Messages.class)
public class MessagesList extends VerticalLayout implements AfterNavigationObserver {
    private static final long serialVersionUID = 1L;
    private final Messages messagesView;
    private final VaadingConnectorClientUIServiceClient messageService;
    private final Grid<DomibusConnectorClientMessage> messageListGrid;
    private List<DomibusConnectorClientMessage> clientMessageList = null;
    TextField searchEbmsIdText = new TextField();
    TextField searchBackendMessageIdText = new TextField();
    TextField searchConversationIdText = new TextField();
    DatePicker fromDatePicker;
    DatePicker toDatePicker;
    TextField messageStatusFilterText = new TextField();
    TextField fromPartyIdFilterText = new TextField();
    TextField toPartyIdFilterText = new TextField();
    TextField serviceFilterText = new TextField();
    TextField serviceTypeFilterText = new TextField();
    TextField actionFilterText = new TextField();

    /**
     * The MessagesList class represents a component that displays a list of messages in the Domibus
     * connector client UI. It is responsible for managing and interacting with the messages
     * displayed in the list.
     *
     * @param messagesView   The Messages view component that displays the list of messages.
     * @param messageService The service client for retrieving and managing messages.
     */
    public MessagesList(
        @Autowired Messages messagesView,
        @Autowired VaadingConnectorClientUIServiceClient messageService) {
        this.messagesView = messagesView;
        this.messageService = messageService;

        this.messagesView.setMessagesListView(this);

        clientMessageList = messageService.getAllMessages().getMessages();

        messageListGrid = createMessageList();
        messageListGrid.setItems(clientMessageList);

        VerticalLayout search = createSearchLayout();
        add(search);

        HorizontalLayout filtering = createFilterLayout();

        var mainLayout = new VerticalLayout(filtering, messageListGrid);
        mainLayout.setAlignItems(Alignment.STRETCH);
        mainLayout.setHeight(ViewConstant.LENGTH_700_PX);
        add(mainLayout);
        setHeight(ViewConstant.LENGTH_100_VH);
        setWidth(ViewConstant.LENGTH_100_VW);
        reloadFullList();
    }

    private VerticalLayout createSearchLayout() {
        searchEbmsIdText.setPlaceholder("Search by EBMS Message ID");
        searchEbmsIdText.setWidth(ViewConstant.LENGTH_300_PX);

        var ebmsIdSearch = new HorizontalLayout();
        ebmsIdSearch.add(searchEbmsIdText);

        var searchEbmsIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
        searchEbmsIdBtn.addClickListener(e -> searchByEbmsId());
        ebmsIdSearch.add(searchEbmsIdBtn);

        var searchLayout = new VerticalLayout();
        searchLayout.add(ebmsIdSearch);

        var backendMessageIdSearch = new HorizontalLayout();

        searchBackendMessageIdText.setPlaceholder("Search by Backend Message ID");
        searchBackendMessageIdText.setWidth(ViewConstant.LENGTH_300_PX);
        backendMessageIdSearch.add(searchBackendMessageIdText);

        var searchBackendMessageIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
        searchBackendMessageIdBtn.addClickListener(e -> searchByBackendMessageId());
        backendMessageIdSearch.add(searchBackendMessageIdBtn);

        searchLayout.add(backendMessageIdSearch);

        var conversationIdSearch = new HorizontalLayout();

        searchConversationIdText.setPlaceholder("Search by Conversation ID");
        searchConversationIdText.setWidth(ViewConstant.LENGTH_300_PX);
        conversationIdSearch.add(searchConversationIdText);

        var searchConversationIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
        searchConversationIdBtn.addClickListener(e -> searchByConversationId());
        conversationIdSearch.add(searchConversationIdBtn);

        searchLayout.add(conversationIdSearch);

        fromDatePicker = new DatePicker();
        fromDatePicker.setLabel("From Date");
        fromDatePicker.setErrorMessage("From Date invalid!");

        var dateSearch = new HorizontalLayout();
        dateSearch.add(fromDatePicker);

        toDatePicker = new DatePicker();
        toDatePicker.setLabel("To Date");
        toDatePicker.setErrorMessage("To Date invalid!");
        dateSearch.add(toDatePicker);

        var searchPeriodBtn = new Button(new Icon(VaadinIcon.SEARCH));
        searchPeriodBtn.addClickListener(e -> searchByPeriod());
        dateSearch.add(searchPeriodBtn);

        dateSearch.setAlignItems(Alignment.END);

        searchLayout.add(dateSearch);

        var resetSearch = new Button(
            new Icon(VaadinIcon.CLOSE_CIRCLE));
        resetSearch.setText("Reset Search");
        resetSearch.addClickListener(e -> {
            searchEbmsIdText.clear();
            searchBackendMessageIdText.clear();
            searchConversationIdText.clear();
            fromDatePicker.clear();
            toDatePicker.clear();
            reloadFullList();
        });

        searchLayout.add(resetSearch);

        return searchLayout;
    }

    private void searchByBackendMessageId() {
        searchEbmsIdText.clear();
        searchConversationIdText.clear();
        fromDatePicker.clear();
        toDatePicker.clear();
        DomibusConnectorClientMessage message = null;
        try {
            message =
                messageService.getMessageByBackendMessageId(searchBackendMessageIdText.getValue());
        } catch (ConnectorClientServiceClientException e) {
            openErrorDialog(e.getMessage());
            return;
        }
        messagesView.showMessageDetails(message.getId());
    }

    private void searchByEbmsId() {
        searchBackendMessageIdText.clear();
        searchConversationIdText.clear();
        fromDatePicker.clear();
        toDatePicker.clear();
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
        searchEbmsIdText.clear();
        searchBackendMessageIdText.clear();
        searchConversationIdText.clear();

        var fromDate = asDate(this.fromDatePicker.getValue());

        if (fromDate == null) {
            fromDate = new Date(1);
        }

        var toDate = asDate(this.toDatePicker.getValue());

        if (toDate == null) {
            toDate = new Date(System.currentTimeMillis());
        } else {
            toDate = new Date(toDate.getTime() + TimeUnit.DAYS.toMillis(1));
        }
        DomibusConnectorClientMessageList fullList;
        try {
            fullList = messageService.getMessagesByPeriod(fromDate, toDate);
        } catch (ConnectorClientServiceClientException e) {
            openErrorDialog(e.getMessage());
            return;
        }
        messageListGrid.setItems(fullList.getMessages());
    }

    private void searchByConversationId() {
        searchEbmsIdText.clear();
        searchBackendMessageIdText.clear();
        fromDatePicker.clear();
        toDatePicker.clear();
        DomibusConnectorClientMessageList fullList;
        try {
            fullList =
                messageService.getMessagesByConversationId(searchConversationIdText.getValue());
        } catch (ConnectorClientServiceClientException e) {
            openErrorDialog(e.getMessage());
            return;
        }
        messageListGrid.setItems(fullList.getMessages());
    }

    private void openErrorDialog(String message) {
        var errorDialog =
            messagesView.getErrorDialog("Error retrieve Message from connector client", message);
        var okButton = new Button("OK");
        okButton.addClickListener(event -> errorDialog.close());

        errorDialog.add(okButton);

        errorDialog.open();
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    private HorizontalLayout createFilterLayout() {

        messageStatusFilterText.setPlaceholder("Filter by MessageStatus");
        messageStatusFilterText.setWidth(ViewConstant.LENGTH_180_PX);
        messageStatusFilterText.setValueChangeMode(ValueChangeMode.EAGER);
        messageStatusFilterText.addValueChangeListener(e -> filter());

        fromPartyIdFilterText.setPlaceholder("Filter by From Party ID");
        fromPartyIdFilterText.setWidth(ViewConstant.LENGTH_180_PX);
        fromPartyIdFilterText.setValueChangeMode(ValueChangeMode.EAGER);
        fromPartyIdFilterText.addValueChangeListener(e -> filter());

        toPartyIdFilterText.setPlaceholder("Filter by To Party ID");
        toPartyIdFilterText.setWidth(ViewConstant.LENGTH_180_PX);
        toPartyIdFilterText.setValueChangeMode(ValueChangeMode.EAGER);
        toPartyIdFilterText.addValueChangeListener(e -> filter());

        serviceFilterText.setPlaceholder("Filter by Service");
        serviceFilterText.setWidth(ViewConstant.LENGTH_180_PX);
        serviceFilterText.setValueChangeMode(ValueChangeMode.EAGER);
        serviceFilterText.addValueChangeListener(e -> filter());

        serviceTypeFilterText.setPlaceholder("Filter by Service Type");
        serviceTypeFilterText.setWidth(ViewConstant.LENGTH_180_PX);
        serviceTypeFilterText.setValueChangeMode(ValueChangeMode.EAGER);
        serviceTypeFilterText.addValueChangeListener(e -> filter());

        actionFilterText.setPlaceholder("Filter by Action");
        actionFilterText.setWidth(ViewConstant.LENGTH_180_PX);
        actionFilterText.setValueChangeMode(ValueChangeMode.EAGER);
        actionFilterText.addValueChangeListener(e -> filter());

        var clearAllFilterTextBtn = new Button(new Icon(VaadinIcon.CLOSE_CIRCLE));
        clearAllFilterTextBtn.setText("Clear Filter");
        clearAllFilterTextBtn.addClickListener(e -> {
            messageStatusFilterText.clear();
            fromPartyIdFilterText.clear();
            toPartyIdFilterText.clear();
            serviceFilterText.clear();
            actionFilterText.clear();
        });

        var refreshListBtn = new Button(new Icon(VaadinIcon.REFRESH));
        refreshListBtn.setText("RefreshList");
        refreshListBtn.addClickListener(e -> reloadList());

        var filtering = new HorizontalLayout(
            messageStatusFilterText,
            fromPartyIdFilterText,
            toPartyIdFilterText,
            serviceFilterText,
            actionFilterText,
            clearAllFilterTextBtn,
            refreshListBtn
        );
        filtering.setWidth(ViewConstant.LENGTH_100_VW);

        return filtering;
    }

    private void filter() {
        LinkedList<DomibusConnectorClientMessage> target = new LinkedList<>();
        for (DomibusConnectorClientMessage msg : clientMessageList) {
            if ((messageStatusFilterText.getValue().isEmpty()
                || msg.getMessageStatus() != null && msg.getMessageStatus().toUpperCase().contains(
                messageStatusFilterText.getValue().toUpperCase())
            )
                && (fromPartyIdFilterText.getValue().isEmpty()
                || msg.getFromPartyId() != null && msg.getFromPartyId().toUpperCase().contains(
                fromPartyIdFilterText.getValue().toUpperCase())
            )
                && (toPartyIdFilterText.getValue().isEmpty()
                || msg.getToPartyId() != null && msg.getToPartyId().toUpperCase().contains(
                toPartyIdFilterText.getValue().toUpperCase())
            )
                && (serviceFilterText.getValue().isEmpty()
                || msg.getService() != null && msg.getService().toUpperCase().contains(
                serviceFilterText.getValue().toUpperCase())
            )
                && (serviceTypeFilterText.getValue().isEmpty()
                || msg.getServiceType() != null && msg.getServiceType().toUpperCase().contains(
                serviceTypeFilterText.getValue().toUpperCase())
            )
                && (actionFilterText.getValue().isEmpty()
                || msg.getAction() != null && msg.getAction().toUpperCase().contains(
                actionFilterText.getValue().toUpperCase())
            )
            ) {
                target.addLast(msg);
            }
        }

        messageListGrid.setItems(target);
    }

    /**
     * Creates a Grid component that displays a list of DomibusConnectorClientMessage objects.
     *
     * @return a Grid&lt;DomibusConnectorClientMessage&gt; component that represents the message
     *      list
     */
    public Grid<DomibusConnectorClientMessage> createMessageList() {
        Grid<DomibusConnectorClientMessage> grid = new Grid<>();

        grid.addComponentColumn(
                domibusConnectorClientMessage ->
                    getDetailsLink(domibusConnectorClientMessage.getId())
            )
            .setHeader("Details").setWidth(ViewConstant.LENGTH_50_PX);
        grid.addComponentColumn(domibusConnectorClientMessage -> getDeleteMessageLink(
            domibusConnectorClientMessage.getId())).setHeader("Delete")
            .setWidth(ViewConstant.LENGTH_50_PX);
        grid.addComponentColumn(this::getEditMessageLink)
            .setHeader("Edit").setWidth(ViewConstant.LENGTH_50_PX);
        grid.addColumn(DomibusConnectorClientMessage::getEbmsMessageId).setHeader("ebmsMessageID")
            .setWidth(ViewConstant.LENGTH_150_PX);
        grid.addColumn(DomibusConnectorClientMessage::getBackendMessageId)
            .setHeader("backendMessageID").setWidth(ViewConstant.LENGTH_150_PX);
        grid.addColumn(DomibusConnectorClientMessage::getConversationId).setHeader("conversationID")
            .setWidth(ViewConstant.LENGTH_150_PX);
        grid.addColumn(DomibusConnectorClientMessage::getFromPartyId).setHeader("From Party ID")
            .setWidth(ViewConstant.LENGTH_100_PX);
        grid.addColumn(DomibusConnectorClientMessage::getToPartyId).setHeader("To Party ID")
            .setWidth(ViewConstant.LENGTH_100_PX);
        grid.addColumn(DomibusConnectorClientMessage::getService).setHeader("Service")
            .setWidth(ViewConstant.LENGTH_100_PX);
        grid.addColumn(DomibusConnectorClientMessage::getServiceType).setHeader("Service Type")
            .setWidth(ViewConstant.LENGTH_100_PX);
        grid.addColumn(DomibusConnectorClientMessage::getAction).setHeader("Action")
            .setWidth(ViewConstant.LENGTH_100_PX);
        grid.addColumn(DomibusConnectorClientMessage::getCreated).setHeader("Created")
            .setWidth(ViewConstant.LENGTH_150_PX);
        grid.addColumn(DomibusConnectorClientMessage::getMessageStatus).setHeader("Message status")
            .setWidth(ViewConstant.LENGTH_100_PX);
        grid.addColumn(DomibusConnectorClientMessage::getLastConfirmationReceived)
            .setHeader("last confirmation received").setWidth(ViewConstant.LENGTH_150_PX);
        grid.addColumn(DomibusConnectorClientMessage::getStorageInfo).setHeader("Storage info")
            .setWidth(ViewConstant.LENGTH_150_PX);
        grid.addColumn(DomibusConnectorClientMessage::getStorageStatus).setHeader("Storage status")
            .setWidth(ViewConstant.LENGTH_100_PX);
        grid.setWidth("2480px");
        grid.setHeight(ViewConstant.LENGTH_700_PX);
        grid.setMultiSort(true);

        for (Column<DomibusConnectorClientMessage> col : grid.getColumns()) {
            col.setSortable(true);
            col.setResizable(true);
        }

        return grid;
    }

    /**
     * Returns a button that displays details of a specific message when clicked.
     *
     * @param connectorMessageId the ID of the message to display details for
     * @return a Button component that, when clicked, displays the details of the specified message
     */
    public Button getDetailsLink(Long connectorMessageId) {
        var getDetails = new Button(new Icon(VaadinIcon.SEARCH));
        getDetails.addClickListener(e -> this.messagesView.showMessageDetails(connectorMessageId));
        return getDetails;
    }

    private Button getDeleteMessageLink(Long l) {
        var deleteMessageButton = new Button(new Icon(VaadinIcon.ERASER));
        deleteMessageButton.addClickListener(e -> {
            var deleteMessageDialog = this.messagesView.getDeleteMessageDialog();
            var delButton = new Button("Delete Message");
            delButton.addClickListener(e1 -> {
                try {
                    this.messageService.deleteMessageById(l);
                } catch (ConnectorClientServiceClientException e2) {
                    var errorDiag = new Dialog();
                    var resultLabel = new LumoLabel();
                    resultLabel.setText("Delete message failed: " + e2.getMessage());
                    resultLabel.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_RED);
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
        var editMessageButton = new Button(new Icon(VaadinIcon.EDIT));
        editMessageButton.addClickListener(
            e -> this.messagesView.showSendMessage(domibusConnectorClientMessage.getId()));
        editMessageButton.setEnabled(domibusConnectorClientMessage.getMessageStatus() != null
                                         && domibusConnectorClientMessage.getMessageStatus()
                                                                         .equalsIgnoreCase(
                                                                             "PREPARED")
        );
        return editMessageButton;
    }

    private void reloadList() {
        if (!searchEbmsIdText.isEmpty()) {
            searchByEbmsId();
        } else if (!searchBackendMessageIdText.isEmpty()) {
            searchByBackendMessageId();
        } else if (!searchConversationIdText.isEmpty()) {
            searchByConversationId();
        } else if (!fromDatePicker.isEmpty() || !toDatePicker.isEmpty()) {
            searchByPeriod();
        } else {
            reloadFullList();
        }
    }

    public void reloadFullList() {
        messageListGrid.setItems(messageService.getAllMessages().getMessages());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        reloadFullList();
        if (this.messagesView.getMessageDetailsView() != null) {
            this.messagesView.getMessageDetailsView().setVisible(false);
        }
        if (this.messagesView.getSendMessageView() != null) {
            this.messagesView.getSendMessageView().setVisible(false);
        }
        this.setVisible(true);
    }
}
