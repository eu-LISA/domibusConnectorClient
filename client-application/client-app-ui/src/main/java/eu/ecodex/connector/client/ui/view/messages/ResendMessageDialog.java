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
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageFileType;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.ecodex.connector.client.ui.ViewConstant;
import eu.ecodex.connector.client.ui.component.LumoLabel;
import eu.ecodex.connector.client.ui.service.VaadingConnectorClientUIServiceClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a dialog for resending a message in a Vaadin UI.
 */
public class ResendMessageDialog extends VerticalLayout {
    private static final long serialVersionUID = 1L;
    private final Messages messagesView;
    private final VaadingConnectorClientUIServiceClient messageService;
    private final Dialog messageDialog;
    private final Map<String, DomibusConnectorClientMessageFileType> selectedFiles =
        new HashMap<>();
    List<DomibusConnectorClientMessageFile> files = null;
    private final DomibusConnectorClientMessage originalMessage;

    /**
     * Constructor.
     *
     * @param messageDialog  the parent dialog that contains the resend message dialog
     * @param message        the original message to be resent
     * @param messagesView   the view that displays the messages
     * @param messageService the client service for interacting with the Domibus Connector Client
     */
    public ResendMessageDialog(
        Dialog messageDialog, DomibusConnectorClientMessage message, Messages messagesView,
        VaadingConnectorClientUIServiceClient messageService) {
        this.messagesView = messagesView;
        this.messageService = messageService;
        this.messageDialog = messageDialog;
        originalMessage = message;
        var info = new Div();
        info.setWidth(ViewConstant.LENGTH_100_VW);
        var infoLabel = new LumoLabel();
        infoLabel.setText(
            "Creating a new message to be sent. All values of the message except unique message "
                + "ids will be filled with attributes from the original message."
        );
        infoLabel.getStyle().set("font-size", ViewConstant.LENGTH_20_PX);
        info.add(infoLabel);

        add(info);

        boolean filesEnabled =
            message.getStorageInfo() != null && !message.getStorageInfo().isEmpty()
                && message.getStorageStatus()
                          .equals(DomibusConnectorClientStorageStatus.STORED.name());

        if (filesEnabled) {
            var filesDiv = new Div();
            filesDiv.setWidth(ViewConstant.LENGTH_100_VW);
            var filesLabel = new LumoLabel();
            filesLabel.setText(
                "Select files from the original message to be sent with the new message:");
            filesLabel.getStyle().set("font-size", ViewConstant.LENGTH_20_PX);
            filesDiv.add(filesLabel);

            add(filesDiv);

            var details = new Div();
            details.setWidth(ViewConstant.LENGTH_100_VW);

            Grid<DomibusConnectorClientMessageFile> grid = new Grid<>();

            grid.setItems(message.getFiles().getFiles());

            grid.addComponentColumn(this::createSelectionCheckbox).setHeader("Filename")
                .setWidth(ViewConstant.LENGTH_30_PX);
            grid.addColumn(DomibusConnectorClientMessageFile::getFileName).setHeader("Filename")
                .setWidth(ViewConstant.LENGTH_500_PX);
            grid.addColumn(DomibusConnectorClientMessageFile::getFileType).setHeader("Filetype")
                .setWidth(ViewConstant.LENGTH_450_PX);

            grid.setWidth(ViewConstant.LENGTH_1000_PX);
            grid.setMultiSort(true);

            for (Column<DomibusConnectorClientMessageFile> col : grid.getColumns()) {
                col.setSortable(true);
                col.setResizable(true);
            }

            details.add(grid);

            add(details);
        }

        var createReply = new Button("Create message");
        createReply.addClickListener(e -> createNewMessage());

        add(createReply);
    }

    private Checkbox createSelectionCheckbox(DomibusConnectorClientMessageFile file) {
        var selection = new Checkbox();

        selection.addValueChangeListener(e -> {
            if (e.getValue()) {
                selectedFiles.put(file.getFileName(), file.getFileType());
            } else {
                selectedFiles.remove(file.getFileName());
            }
        });

        return selection;
    }

    private void createNewMessage() {
        DomibusConnectorClientMessage replyMessage = createNewReplyMessage();

        if (!selectedFiles.isEmpty()) {
            files = new ArrayList<>();
            selectedFiles.keySet().forEach(fileName -> {
                byte[] fileContent = this.messageService.loadFileContentFromStorageLocation(
                    originalMessage.getStorageInfo(), fileName);
                var clientMessageFile =
                    new DomibusConnectorClientMessageFile(
                        fileName,
                        selectedFiles.get(fileName),
                        fileContent
                    );
                files.add(clientMessageFile);
            });
        }

        replyMessage.getFiles().setFiles(files);
        replyMessage = messageService.saveMessage(replyMessage);

        messagesView.showSendMessage(replyMessage.getId());

        messageDialog.close();
    }

    private DomibusConnectorClientMessage createNewReplyMessage() {
        var replyMessage = new DomibusConnectorClientMessage();

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
