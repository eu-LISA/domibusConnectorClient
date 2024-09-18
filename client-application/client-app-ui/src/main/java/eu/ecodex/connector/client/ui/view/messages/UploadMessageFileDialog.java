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

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import eu.ecodex.connector.client.rest.model.DomibusConnectorClientMessageFileType;
import eu.ecodex.connector.client.ui.ViewConstant;
import eu.ecodex.connector.client.ui.component.LumoLabel;
import java.io.ByteArrayOutputStream;
import lombok.Data;

/**
 * The UploadMessageFileDialog class represents a dialog that allows users to upload a file to a
 * message.
 */
@Data
public class UploadMessageFileDialog extends Dialog {
    private static final long serialVersionUID = 1L;
    ComboBox<DomibusConnectorClientMessageFileType> fileType;
    byte[] fileContents = null;
    String fileName;
    LumoLabel resultLabel;
    Div areaResult;

    /**
     * Constructor.
     */
    public UploadMessageFileDialog() {
        var headerContent = new Div();
        var header = new NativeLabel("Upload file to message");
        header.getStyle().set("font-weight", "bold");
        header.getStyle().set("font-style", "italic");
        headerContent.getStyle().set("text-align", "center");
        headerContent.getStyle().set("padding", ViewConstant.LENGTH_10_PX);
        headerContent.add(header);
        add(headerContent);

        fileType = new ComboBox<>();
        fileType.setItems(DomibusConnectorClientMessageFileType.values());
        fileType.setLabel("File Type");
        fileType.setWidth(ViewConstant.LENGTH_500_PX);

        var buffer = new MemoryBuffer();
        var upload = new Upload(buffer);

        fileType.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                if (e.getValue().equals(DomibusConnectorClientMessageFileType.BUSINESS_CONTENT)
                    || e.getValue()
                        .equals(DomibusConnectorClientMessageFileType.DETACHED_SIGNATURE)) {
                    upload.setAcceptedFileTypes("application/xml", "text/xml");
                    fileType.setInvalid(false);
                    upload.setVisible(true);
                } else if (e.getValue()
                            .equals(DomibusConnectorClientMessageFileType.CONFIRMATION)) {
                    fileType.setErrorMessage(
                        "Invalid file type: Confirmations must not be uploaded as part of the "
                            + "message! If a confirmation should be part of this message, add it "
                            + "as BUSINESS_ATTACHMENT!"
                    );
                    fileType.setInvalid(true);
                    upload.setVisible(false);
                } else if (e.getValue()
                            .equals(DomibusConnectorClientMessageFileType.BUSINESS_DOCUMENT)) {
                    upload.setAcceptedFileTypes("application/pdf");
                    fileType.setInvalid(false);
                    upload.setVisible(true);
                } else {
                    upload.setAcceptedFileTypes();
                    fileType.setInvalid(false);
                    upload.setVisible(true);
                }
            }
        });

        var areaFileType = new Div();
        areaFileType.add(fileType);

        add(areaFileType);

        upload.setMaxFiles(1);
        upload.setId("File-Upload");
        upload.setVisible(false);

        areaResult = new Div();
        resultLabel = new LumoLabel();

        upload.addSucceededListener(event -> {
            fileContents = ((ByteArrayOutputStream) buffer.getFileData().getOutputBuffer())
                .toByteArray();
            fileName = buffer.getFileName();
            resultLabel.setText("File uploaded");
            resultLabel.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_GREEN);
            areaResult.add(resultLabel);
        });
        upload.addFailedListener(e -> {
            resultLabel.setText("File upload failed!");
            resultLabel.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_RED);
            areaResult.add(resultLabel);
        });

        var areaImporter = new Div();
        areaImporter.add(upload);

        add(areaImporter);
        add(areaResult);
    }

    /**
     * Sets the error text for the error message in the upload message file dialog.
     *
     * @param errorMessage the error message to be displayed
     */
    public void setErrorText(String errorMessage) {
        resultLabel.setText(errorMessage);
        resultLabel.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_RED);
        areaResult.add(resultLabel);
    }
}
