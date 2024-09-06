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
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageFileType;
import eu.ecodex.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.ecodex.connector.client.ui.ViewConstant;
import eu.ecodex.connector.client.ui.component.LumoLabel;
import eu.ecodex.connector.client.ui.form.DynamicMessageForm;
import eu.ecodex.connector.client.ui.service.ConnectorClientServiceClientException;
import eu.ecodex.connector.client.ui.service.VaadingConnectorClientUIServiceClient;
import java.io.ByteArrayInputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The {@code SendMessage} class is a UI component that allows users to send messages.
 *
 * @see VerticalLayout
 * @see HasUrlParameter
 * @see AfterNavigationObserver
 * @see Component
 * @see UIScope
 * @see Route
 * @see com.vaadin.flow.component.formlayout.FormLayout
 */
@Component
@UIScope
@Route(value = SendMessage.ROUTE, layout = Messages.class)
public class SendMessage extends VerticalLayout
    implements HasUrlParameter<Long>, AfterNavigationObserver {
    private static final long serialVersionUID = 1L;
    public static final String ROUTE = "sendMessage";
    private final VaadingConnectorClientUIServiceClient messageService;
    private final Messages messagesView;
    private final DynamicMessageForm messageForm = new DynamicMessageForm();
    private final VerticalLayout messageFilesArea = new VerticalLayout();
    boolean filesEnabled = false;
    boolean saveEnabled = true;
    Button saveBtn;
    Button uploadFileButton;
    Button submitMessageButton;
    Div resultArea;

    /**
     * Constructor.
     *
     * @param messagesView   The Messages object used for displaying messages in the UI. Must be
     *                       autowired.
     * @param messageService The VaadingConnectorClientUIServiceClient object used for sending
     *                       messages. Must be autowired.
     */
    public SendMessage(
        @Autowired Messages messagesView,
        @Autowired VaadingConnectorClientUIServiceClient messageService) {
        this.messagesView = messagesView;
        this.messageService = messageService;

        this.messagesView.setSendMessageView(this);

        var messageDetailsArea = new VerticalLayout();
        messageForm.getStyle().set("margin-top", ViewConstant.LENGTH_25_PX);

        messageDetailsArea.add(messageForm);
        messageForm.setEnabled(true);
        messageDetailsArea.setWidth(ViewConstant.LENGTH_500_PX);
        add(messageDetailsArea);

        saveBtn = new Button(new Icon(VaadinIcon.EDIT));
        saveBtn.setText("Save Message");
        saveBtn.addClickListener(e -> {
            BinderValidationStatus<DomibusConnectorClientMessage> validationStatus =
                messageForm.getBinder().validate();
            if (validationStatus.isOk()) {
                DomibusConnectorClientMessage msg =
                    this.messageService.saveMessage(messageForm.getConnectorClientMessage());
                var result = new LumoLabel();
                if (msg != null) {
                    messageForm.setConnectorClientMessage(msg);
                    result.setText("Message successfully saved!");
                    result.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_GREEN);
                } else {
                    result.setText("Save message failed!");
                    result.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_RED);
                }
                loadPreparedMessage(messageForm.getConnectorClientMessage().getId(), result);
            }
        });
        saveBtn.setEnabled(saveEnabled);

        uploadFileButton = new Button(new Icon(VaadinIcon.UPLOAD));
        uploadFileButton.setText("Add File to message");
        uploadFileButton.addClickListener(e -> {
            var uploadFileDialog = new UploadMessageFileDialog();
            var uploadButton = new Button(new Icon(VaadinIcon.UPLOAD));
            uploadButton.setText("Add File to message");
            uploadButton.addClickListener(e1 -> {
                if (!StringUtils.isEmpty(uploadFileDialog.getFileName())
                    && uploadFileDialog.getFileType().getValue() != null
                    && uploadFileDialog.getFileContents() != null
                    && uploadFileDialog.getFileContents().length > 0) {
                    String nok = checkFileValid(
                        uploadFileDialog.getFileName(),
                        uploadFileDialog.getFileType().getValue()
                    );
                    var resultLabel = new LumoLabel();
                    if (nok == null) {
                        var messageFile = new DomibusConnectorClientMessageFile(
                            uploadFileDialog.getFileName(),
                            uploadFileDialog.getFileType().getValue(),
                            uploadFileDialog.getFileContents()
                        );
                        messageFile.setStorageLocation(
                            messageForm.getConnectorClientMessage().getStorageInfo());
                        boolean result = this.messageService.uploadFileToMessage(messageFile);
                        if (!result) {
                            resultLabel.setText("Add file to message failed!");
                            resultLabel.getStyle()
                                       .set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_RED);
                        } else {
                            resultLabel.setText("File successfully added to message");
                            resultLabel.getStyle()
                                       .set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_GREEN);
                        }
                    } else {
                        resultLabel.setText(nok);
                        resultLabel.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_RED);
                    }
                    uploadFileDialog.close();
                    loadPreparedMessage(
                        messageForm.getConnectorClientMessage().getId(), resultLabel);
                }
            });
            uploadFileDialog.add(uploadButton);
            uploadFileDialog.open();
        });
        uploadFileButton.setEnabled(filesEnabled);

        submitMessageButton = new Button(new Icon(VaadinIcon.CLOUD_UPLOAD_O));
        submitMessageButton.setText("Submit Message");
        submitMessageButton.addClickListener(e -> {
            if (validateMessageForm()) {
                var resultLabel = new LumoLabel();
                if (!validateMessageForSubmission()) {
                    resultLabel.setText(
                        "For message submission a BUSINESS_CONTENT and BUSINESS_DOCUMENT must be"
                            + " present!"
                    );
                    resultLabel.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_RED);
                } else {
                    try {
                        DomibusConnectorClientMessage msg = this.messageService.saveMessage(
                            messageForm.getConnectorClientMessage());
                        this.messageService.submitStoredMessage(msg);
                        resultLabel.setText("Message successfully submitted!");
                        resultLabel.getStyle()
                                   .set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_GREEN);
                    } catch (ConnectorClientServiceClientException e1) {
                        resultLabel.setText(
                            "Exception thrown at connector client: " + e1.getMessage());
                        resultLabel.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_RED);
                    }
                }
                loadPreparedMessage(messageForm.getConnectorClientMessage().getId(), resultLabel);
            }
        });
        submitMessageButton.setEnabled(filesEnabled && validateMessageForm());

        var buttons = new HorizontalLayout(
            saveBtn, uploadFileButton, submitMessageButton
        );
        buttons.setWidth(ViewConstant.LENGTH_100_VW);
        add(buttons);

        resultArea = new Div();
        add(resultArea);
        add(messageFilesArea);
        setSizeFull();
    }

    private boolean validateMessageForm() {
        BinderValidationStatus<DomibusConnectorClientMessage> validationStatus =
            messageForm.getBinder().validate();
        return validationStatus.isOk();
    }

    private String checkFileValid(String fileName, DomibusConnectorClientMessageFileType fileType) {
        if (messageForm.getConnectorClientMessage().getFiles() != null
            && messageForm.getConnectorClientMessage().getFiles().getFiles() != null) {

            for (DomibusConnectorClientMessageFile file : messageForm.getConnectorClientMessage()
                                                                     .getFiles().getFiles()) {
                if (file.getFileName().equals(fileName)) {
                    return "File with that name already part of the message!";
                }
                switch (fileType) {
                    case BUSINESS_CONTENT:
                        if (file.getFileType().equals(fileType)) {
                            return "BUSINESS_CONTENT already part of the message! Must not be more"
                                + " than one!";
                        }
                        break;
                    case BUSINESS_DOCUMENT:
                        if (file.getFileType().equals(fileType)) {
                            return "BUSINESS_DOCUMENT already part of the message! Must not be "
                                + "more than one!";
                        }
                        break;
                    case DETACHED_SIGNATURE:
                        if (file.getFileType().equals(fileType)) {
                            return "DETACHED_SIGNATURE already part of the message! Must not be "
                                + "more than one!";
                        }
                        break;
                    default:
                }
            }
        }
        return null;
    }

    private Button getDeleteFileLink(DomibusConnectorClientMessageFile file) {
        var deleteFileButton = new Button(new Icon(VaadinIcon.ERASER));
        deleteFileButton.setEnabled(saveEnabled);
        deleteFileButton.addClickListener(e -> {
            var headerContent = new Div();
            var headerLabel = new Label("Delete file from message");
            headerLabel.getStyle().set("font-weight", "bold");
            headerLabel.getStyle().set("font-style", "italic");
            headerContent.getStyle().set("text-align", "center");
            headerContent.getStyle().set("padding", ViewConstant.LENGTH_10_PX);
            headerContent.add(headerLabel);

            var deleteMessageDialog = new Dialog();
            deleteMessageDialog.add(headerContent);

            var labelContent = new Div();
            var label = new LumoLabel(
                "Are you sure you want to delete this file from the message? Storage file is "
                    + "deleted as well!"
            );

            labelContent.add(label);
            deleteMessageDialog.add(labelContent);

            var delButton = new Button("Delete File");
            delButton.addClickListener(e1 -> {
                boolean result = this.messageService.deleteFileFromMessage(file);
                var resultLabel = new LumoLabel();
                if (result) {
                    resultLabel.setText("File " + file.getFileName() + " deleted successfully");
                    resultLabel.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_GREEN);
                } else {
                    resultLabel.setText("Delete file " + file.getFileName() + " failed!");
                    resultLabel.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_RED);
                }
                deleteMessageDialog.close();
                loadPreparedMessage(messageForm.getConnectorClientMessage().getId(), resultLabel);
            });
            deleteMessageDialog.add(delButton);
            deleteMessageDialog.open();
        });
        return deleteFileButton;
    }

    private boolean validateMessageForSubmission() {
        var businessDocumentFound = false;
        var businessContentFound = false;
        for (DomibusConnectorClientMessageFile file : messageForm.getConnectorClientMessage()
                                                                 .getFiles().getFiles()) {
            if (file.getFileType().equals(DomibusConnectorClientMessageFileType.BUSINESS_CONTENT)) {
                businessContentFound = true;
            }
            if (file.getFileType()
                    .equals(DomibusConnectorClientMessageFileType.BUSINESS_DOCUMENT)) {
                businessDocumentFound = true;
            }
        }
        return businessContentFound && businessDocumentFound;
    }

    /**
     * Loads a prepared message from the Domibus Connector Client based on the given connector
     * message ID.
     *
     * @param connectorMessageId the ID of the message to load
     * @param result             the LumoLabel to display the result message, or null if not needed
     */
    public void loadPreparedMessage(Long connectorMessageId, LumoLabel result) {
        DomibusConnectorClientMessage messageByConnectorId;
        try {
            messageByConnectorId = messageService.getMessageById(connectorMessageId);
        } catch (ConnectorClientServiceClientException e) {
            var errorDialog =
                messagesView.getErrorDialog(
                    "Error loading prepared Message from connector client",
                    e.getMessage()
                );
            var okButton = new Button("OK");
            okButton.addClickListener(event -> {
                messagesView.showMessagesList();
                errorDialog.close();
            });

            errorDialog.add(okButton);

            errorDialog.open();
            return;
        }
        messageForm.setConnectorClientMessage(messageByConnectorId);

        filesEnabled = messageForm.getConnectorClientMessage() != null
            && messageForm.getConnectorClientMessage().getStorageInfo() != null
            && !messageForm.getConnectorClientMessage().getStorageInfo().isEmpty()
            && messageForm.getConnectorClientMessage().getStorageStatus()
                          .equals(DomibusConnectorClientStorageStatus.STORED.name());

        saveEnabled = messageForm.getConnectorClientMessage().getMessageStatus().equals("PREPARED");

        saveBtn.setEnabled(saveEnabled);
        uploadFileButton.setEnabled(filesEnabled && saveEnabled);
        submitMessageButton.setEnabled(filesEnabled && saveEnabled);

        buildMessageFilesArea(messageByConnectorId);

        if (result != null) {
            resultArea.removeAll();
            resultArea.add(result);
            resultArea.setVisible(true);
        } else {
            resultArea.removeAll();
            resultArea.setVisible(false);
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

        if (filesEnabled) {
            Grid<DomibusConnectorClientMessageFile> grid = new Grid<>();
            grid.setItems(messageByConnectorId.getFiles().getFiles());
            grid.addComponentColumn(this::getDeleteFileLink).setHeader("Delete")
                .setWidth(ViewConstant.LENGTH_50_PX);
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
        messageFilesArea.setVisible(filesEnabled);
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

    private void clearSendMessage() {
        messageForm.setConnectorClientMessage(new DomibusConnectorClientMessage());

        resultArea.removeAll();
        resultArea.setVisible(false);

        messageFilesArea.removeAll();
        messageFilesArea.setVisible(false);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent arg0) {
        if (this.messagesView.getMessagesListView() != null) {
            this.messagesView.getMessagesListView().setVisible(false);
        }
        if (this.messagesView.getMessageDetailsView() != null) {
            this.messagesView.getMessageDetailsView().setVisible(false);
        }
        this.setVisible(true);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        if (parameter != null) {
            loadPreparedMessage(parameter, null);
        } else {
            clearSendMessage();
        }
    }
}
