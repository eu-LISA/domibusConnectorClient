/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.ui.view.messages;

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
import eu.domibus.connector.client.ui.ViewConstant;
import eu.domibus.connector.client.ui.component.LumoLabel;
import eu.domibus.connector.client.ui.service.VaadingConnectorClientUIServiceClient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The ReplyToMessageDialog class is a custom dialog for replying to a message.
 *
 * <p>This dialog allows the user to create a new message as a reply to an original message. Most
 * of the values for the new message will be pre-filled with attributes from the original message.
 */
public class ReplyToMessageDialog extends VerticalLayout {
    private static final long serialVersionUID = 1L;
    private final Messages messagesView;
    private final VaadingConnectorClientUIServiceClient messageService;
    private final Dialog replyToMessageDialog;
    private final Set<String> selectedFiles = new HashSet<>();
    List<DomibusConnectorClientMessageFile> files = null;
    private final DomibusConnectorClientMessage originalMessage;

    /**
     * Constructor.
     *
     * @param replyToMessageDialog the dialog to reply to the message
     * @param message              the original message to reply to
     * @param messagesView         the view that displays messages
     * @param messageService       the service client for performing message-related operations
     */
    public ReplyToMessageDialog(
        Dialog replyToMessageDialog, DomibusConnectorClientMessage message, Messages messagesView,
        VaadingConnectorClientUIServiceClient messageService) {
        this.messagesView = messagesView;
        this.messageService = messageService;
        this.replyToMessageDialog = replyToMessageDialog;
        originalMessage = message;
        var info = new Div();
        info.setWidth(ViewConstant.LENGTH_100_VW);
        var infoLabel = new LumoLabel();
        infoLabel.setText(
            "Creating a new message to be sent. Most values of the message will be filled with "
                + "attributes from the original message."
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
                "Select files from the original message the reply message should contain as "
                    + "business attachment:"
            );
            filesLabel.getStyle().set("font-size", ViewConstant.LENGTH_20_PX);
            filesDiv.add(filesLabel);

            add(filesDiv);

            var details = new Div();
            details.setWidth(ViewConstant.LENGTH_100_VW);

            Grid<DomibusConnectorClientMessageFile> grid = new Grid<>();

            grid.setItems(message.getFiles().getFiles());

            grid.addComponentColumn(domibusConnectorClientMessageFile -> createSelectionCheckbox(
                    domibusConnectorClientMessageFile.getFileName())).setHeader("Filename")
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

        var createReply = new Button("Create reply");
        createReply.addClickListener(e -> createReply());

        add(createReply);
    }

    private Checkbox createSelectionCheckbox(String fileName) {
        var selection = new Checkbox();

        selection.addValueChangeListener(e -> {
            if (e.getValue()) {
                selectedFiles.add(fileName);
            } else {
                selectedFiles.remove(fileName);
            }
        });

        return selection;
    }

    private void createReply() {
        DomibusConnectorClientMessage replyMessage = createNewReplyMessage();

        if (!selectedFiles.isEmpty()) {
            files = new ArrayList<>();
            selectedFiles.forEach(file -> {
                byte[] fileContent = this.messageService.loadFileContentFromStorageLocation(
                    originalMessage.getStorageInfo(), file);
                var clientMessageFile = new DomibusConnectorClientMessageFile(
                    file,
                    DomibusConnectorClientMessageFileType.BUSINESS_ATTACHMENT,
                    fileContent
                );
                files.add(clientMessageFile);
            });
        }

        replyMessage.getFiles().setFiles(files);
        replyMessage = messageService.saveMessage(replyMessage);

        messagesView.showSendMessage(replyMessage.getId());

        replyToMessageDialog.close();
    }

    private DomibusConnectorClientMessage createNewReplyMessage() {
        var replyMessage = new DomibusConnectorClientMessage();
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
