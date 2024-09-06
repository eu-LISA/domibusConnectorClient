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
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientConfirmation;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.ecodex.connector.client.ui.ViewConstant;
import eu.ecodex.connector.client.ui.component.LumoLabel;
import eu.ecodex.connector.client.ui.form.DomibusConnectorClientMessageForm;
import eu.ecodex.connector.client.ui.service.ConnectorClientServiceClientException;
import eu.ecodex.connector.client.ui.service.VaadingConnectorClientUIServiceClient;
import java.io.ByteArrayInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Represents a vertical layout component that displays the details of a message. It implements the
 * HasUrlParameter interface to handle URL parameters and the AfterNavigationObserver interface to
 * perform actions after navigation to the view is complete.
 */
@Component
@Route(value = MessageDetails.ROUTE, layout = Messages.class)
@UIScope
public class MessageDetails extends VerticalLayout
    implements HasUrlParameter<Long>, AfterNavigationObserver {
    public static final String ROUTE = "messageDetails";
    private static final long serialVersionUID = 1L;
    Messages messagesView;
    VaadingConnectorClientUIServiceClient messageService;
    private final DomibusConnectorClientMessageForm messageForm =
        new DomibusConnectorClientMessageForm();
    private final VerticalLayout messageEvidencesArea = new VerticalLayout();
    private final VerticalLayout messageFilesArea = new VerticalLayout();
    Button replyToMessageButton;
    Button resendMessageButton;
    Button refreshButton;
    Button deleteMessageButton;
    Div resultArea;

    /**
     * Represents the details view of a message.
     *
     * @param messagesView   The Messages view that contains the message details view
     * @param messageService The service client for communicating with the message service
     */
    public MessageDetails(
        @Autowired Messages messagesView,
        @Autowired VaadingConnectorClientUIServiceClient messageService) {

        this.messagesView = messagesView;
        this.messageService = messageService;

        this.messagesView.setMessageDetailsView(this);

        refreshButton = new Button(new Icon(VaadinIcon.REFRESH));
        refreshButton.setText("Refresh");
        refreshButton.addClickListener(
            e -> loadMessageDetails(messageForm.getConnectorClientMessage().getId(), null)
        );
        refreshButton.setEnabled(false);

        deleteMessageButton = new Button(new Icon(VaadinIcon.ERASER));
        deleteMessageButton.setText("Delete");
        deleteMessageButton.addClickListener(e -> {
            var deleteMessageDialog = this.messagesView.getDeleteMessageDialog();
            var delButton = new Button("Delete Message");
            delButton.addClickListener(e1 -> {
                try {
                    this.messageService.deleteMessageById(
                        messageForm.getConnectorClientMessage().getId());
                } catch (ConnectorClientServiceClientException e2) {
                    var resultLabel = new LumoLabel();
                    resultLabel.setText("Delete message failed: " + e2.getMessage());
                    resultLabel.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_RED);
                    loadMessageDetails(
                        messageForm.getConnectorClientMessage().getId(), resultLabel);
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

        var buttons = new HorizontalLayout(
            refreshButton, deleteMessageButton, replyToMessageButton, resendMessageButton
        );
        buttons.setWidth(ViewConstant.LENGTH_100_VW);
        add(buttons);

        resultArea = new Div();

        add(resultArea);

        var messageDetailsArea = new VerticalLayout();
        messageForm.getStyle().set("margin-top", ViewConstant.LENGTH_25_PX);

        messageDetailsArea.add(messageForm);
        messageForm.setEnabled(true);
        messageDetailsArea.setWidth(ViewConstant.LENGTH_500_PX);
        add(messageDetailsArea);

        add(messageFilesArea);

        add(messageEvidencesArea);

        setSizeFull();
    }

    private void openReplyToMessageDialog() {
        var headerContent = new Div();
        var header = new Label("Send reply to a message");
        header.getStyle().set("font-weight", "bold");
        header.getStyle().set("font-style", "italic");
        headerContent.getStyle().set("text-align", "center");
        headerContent.getStyle().set("padding", ViewConstant.LENGTH_10_PX);
        headerContent.add(header);

        var replyToMessageDialog = new Dialog();
        replyToMessageDialog.add(headerContent);

        var view = new ReplyToMessageDialog(
            replyToMessageDialog,
            messageForm.getConnectorClientMessage(),
            messagesView,
            messageService
        );
        replyToMessageDialog.add(view);

        replyToMessageDialog.open();
    }

    private void openResendMessageDialog() {
        var headerContent = new Div();
        var header = new Label("Resend message");
        header.getStyle().set("font-weight", "bold");
        header.getStyle().set("font-style", "italic");
        headerContent.getStyle().set("text-align", "center");
        headerContent.getStyle().set("padding", ViewConstant.LENGTH_10_PX);
        headerContent.add(header);

        var resendMessageDialog = new Dialog();
        resendMessageDialog.add(headerContent);

        var view = new ResendMessageDialog(
            resendMessageDialog,
            messageForm.getConnectorClientMessage(),
            messagesView,
            messageService
        );
        resendMessageDialog.add(view);

        resendMessageDialog.open();
    }

    /**
     * Loads the details of a message with the given ID and displays them on the UI.
     *
     * @param msgId  the ID of the message to load
     * @param result the LumoLabel component to display the result of the operation
     */
    public void loadMessageDetails(Long msgId, LumoLabel result) {
        if (msgId != null) {
            DomibusConnectorClientMessage connectorClientMessage;
            try {
                connectorClientMessage = messageService.getMessageById(msgId);
            } catch (ConnectorClientServiceClientException e) {
                var diag = messagesView.getErrorDialog(
                    "Error retrieve Message from connector client",
                    e.getMessage()
                );
                var okButton = new Button("OK");
                okButton.addClickListener(event -> {
                    messagesView.showMessagesList();
                    diag.close();
                });

                diag.add(okButton);

                diag.open();
                return;
            }
            messageForm.setConnectorClientMessage(connectorClientMessage);

            buildMessageFilesArea(connectorClientMessage);

            buildMessageEvidencesArea(connectorClientMessage);

            refreshButton.setEnabled(true);
            deleteMessageButton.setEnabled(true);
            replyToMessageButton.setEnabled(messageForm.getConnectorClientMessage() != null
                                                && messageForm.getConnectorClientMessage()
                                                              .getMessageStatus()
                                                              .equals("CONFIRMED"));
            resendMessageButton.setEnabled(true);

            if (result != null) {
                resultArea.removeAll();
                resultArea.add(result);
                resultArea.setVisible(true);
            } else {
                resultArea.removeAll();
                resultArea.setVisible(false);
            }
        }
    }

    private void buildMessageFilesArea(DomibusConnectorClientMessage messageByConnectorId) {

        messageFilesArea.removeAll();

        var files = new Div();
        files.setWidth(ViewConstant.LENGTH_100_VW);
        var filesLabel = new LumoLabel();
        filesLabel.setText("Files:");
        filesLabel.getStyle().set("font-size", ViewConstant.LENGTH_20_PX);
        files.add(filesLabel);

        messageFilesArea.add(files);

        var details = new Div();
        details.setWidth(ViewConstant.LENGTH_100_VW);

        boolean filesEnabled =
            messageByConnectorId.getStorageInfo() != null && !messageByConnectorId.getStorageInfo()
                                                                                  .isEmpty()
                && messageByConnectorId.getStorageStatus()
                                       .equals(DomibusConnectorClientStorageStatus.STORED.name());

        if (filesEnabled) {

            Grid<DomibusConnectorClientMessageFile> grid = new Grid<>();

            grid.setItems(messageByConnectorId.getFiles().getFiles());

            grid.addComponentColumn(
                    domibusConnectorClientMessageFile -> createDownloadButton(
                        filesEnabled,
                        domibusConnectorClientMessageFile.getFileName(),
                        messageByConnectorId.getStorageInfo()
                    ))
                .setHeader("Filename").setWidth(ViewConstant.LENGTH_500_PX);
            grid.addColumn(DomibusConnectorClientMessageFile::getFileType).setHeader("Filetype")
                .setWidth(ViewConstant.LENGTH_450_PX);

            grid.setWidth(ViewConstant.LENGTH_1000_PX);
            grid.setMultiSort(true);

            for (Column<DomibusConnectorClientMessageFile> col : grid.getColumns()) {
                col.setSortable(true);
                col.setResizable(true);
            }

            details.add(grid);
        }
        messageFilesArea.add(details);

        messageFilesArea.setWidth(ViewConstant.LENGTH_100_VW);
        messageFilesArea.setVisible(true);
    }

    private Anchor createDownloadButton(boolean enabled, String fileName, String storageLocation) {
        final var resource = new StreamResource(
            fileName,
            () -> new ByteArrayInputStream(
                messageService.loadFileContentFromStorageLocation(storageLocation, fileName))
        );

        var downloadAnchor = new Anchor();
        if (enabled) {
            downloadAnchor.setHref(resource);
        } else {
            downloadAnchor.removeHref();
        }
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.setTarget("_blank");
        downloadAnchor.setTitle(fileName);

        var button = new Label(fileName);
        downloadAnchor.add(button);

        return downloadAnchor;
    }

    private void buildMessageEvidencesArea(DomibusConnectorClientMessage messageByConnectorId) {
        if (!messageByConnectorId.getEvidences().isEmpty()) {
            messageEvidencesArea.removeAll();

            var evidences = new Div();
            evidences.setWidth(ViewConstant.LENGTH_100_VW);
            var evidencesLabel = new LumoLabel();
            evidencesLabel.setText("Evidences:");
            evidencesLabel.getStyle().set("font-size", ViewConstant.LENGTH_20_PX);
            evidences.add(evidencesLabel);

            messageEvidencesArea.add(evidences);

            var details = new Div();
            details.setWidth(ViewConstant.LENGTH_100_VW);

            Grid<DomibusConnectorClientConfirmation> grid = new Grid<>();

            grid.setItems(messageByConnectorId.getEvidences());

            grid.addColumn(DomibusConnectorClientConfirmation::getConfirmationType)
                .setHeader("Confirmation Type").setWidth(ViewConstant.LENGTH_250_PX);
            grid.addColumn(DomibusConnectorClientConfirmation::getReceived).setHeader("Received")
                .setWidth(ViewConstant.LENGTH_300_PX);

            grid.setWidth(ViewConstant.LENGTH_1000_PX);
            grid.setHeight(ViewConstant.LENGTH_210_PX);
            grid.setMultiSort(true);

            for (Column<DomibusConnectorClientConfirmation> col : grid.getColumns()) {
                col.setSortable(true);
                col.setResizable(true);
            }

            details.add(grid);

            messageEvidencesArea.add(details);

            messageEvidencesArea.setWidth(ViewConstant.LENGTH_100_VW);
            messageEvidencesArea.setVisible(true);
        }
    }

    @Override
    public void setParameter(
        BeforeEvent event, @OptionalParameter Long parameter) {
        if (parameter != null) {
            loadMessageDetails(parameter, null);
        } else {
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
        if (this.messagesView.getMessagesListView() != null) {
            this.messagesView.getMessagesListView().setVisible(false);
        }
        if (this.messagesView.getSendMessageView() != null) {
            this.messagesView.getSendMessageView().setVisible(false);
        }
        this.setVisible(true);
    }
}
