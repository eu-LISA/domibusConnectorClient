/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.ui.component;

import com.vaadin.flow.component.html.NativeLabel;
import lombok.NoArgsConstructor;

/**
 * An extension of the {@link NativeLabel} component that provides a custom styling for Lumo theme.
 */
@NoArgsConstructor
public class LumoLabel extends NativeLabel {
    private static final long serialVersionUID = 1L;

    {
        super.getStyle().set(
            "font-family",
            "-apple-system, BlinkMacSystemFont, \"Roboto\", \"Segoe UI\", Helvetica, Arial, "
                + "sans-serif, \"Apple Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\""
        );
    }

    public LumoLabel(String text) {
        super(text);
    }
}
