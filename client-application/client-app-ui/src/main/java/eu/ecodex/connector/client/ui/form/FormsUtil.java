/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.ui.form;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import eu.ecodex.connector.client.ui.ViewConstant;
import lombok.experimental.UtilityClass;

/**
 * The FormsUtil class provides static utility methods for creating various types of form fields
 * with common formatting options.
 */
@UtilityClass
public class FormsUtil {
    /**
     * Returns a formatted text field with common styling options and set as read-only.
     *
     * @return The formatted text field set as read-only.
     */
    public static TextField getFormattedTextFieldReadOnly() {
        var textField = new TextField();
        textField.setReadOnly(true);
        textField.getStyle().set("fontSize", ViewConstant.LENGTH_13_PX);
        textField.setWidth(ViewConstant.LENGTH_600_PX);
        return textField;
    }

    /**
     * Returns a formatted TextField with common styling options.
     *
     * @return The formatted TextField.
     */
    public static TextField getFormattedTextField() {
        var textField = new TextField();
        textField.getStyle().set("fontSize", ViewConstant.LENGTH_13_PX);
        textField.setWidth(ViewConstant.LENGTH_600_PX);
        return textField;
    }

    /**
     * Returns a {@code TextField} with common formatting options and the {@code required} flag set
     * to true.
     *
     * @return The formatted required TextField.
     */
    public static TextField getFormattedRequiredTextField() {
        var textField = getFormattedTextField();
        textField.setRequired(true);
        return textField;
    }

    /**
     * Returns a TextArea with formatted styling.
     *
     * @return The formatted TextArea.
     */
    public static TextArea getFormattedTextArea() {
        var textArea = new TextArea();
        textArea.getStyle().set("fontSize", ViewConstant.LENGTH_13_PX);
        textArea.setWidth(ViewConstant.LENGTH_600_PX);
        return textArea;
    }
}
