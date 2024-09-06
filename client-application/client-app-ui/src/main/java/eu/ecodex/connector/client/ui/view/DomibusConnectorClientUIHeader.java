/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.ui.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.client.ui.ViewConstant;
import eu.ecodex.connector.client.ui.component.LumoLabel;

/**
 * The DomibusConnectorClientUIHeader class is a custom component that represents the header section
 * of the Domibus Connector Client UI.
 */
@UIScope
@org.springframework.stereotype.Component
public class DomibusConnectorClientUIHeader extends HorizontalLayout {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public DomibusConnectorClientUIHeader() {
        var ecodexLogo = new Div();
        var ecodex = new Image("frontend/images/logo_ecodex_0.png", "eCodex");
        ecodex.setHeight("70px");
        ecodexLogo.add(ecodex);
        ecodexLogo.setHeight("70px");

        var clientUiLabel = new LumoLabel("domibusConnectorClient UI");
        clientUiLabel.getStyle().set("font-size", ViewConstant.LENGTH_30_PX);
        clientUiLabel.getStyle().set("font-style", "italic");
        clientUiLabel.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_GREY);

        var domibusConnector = new Div();
        domibusConnector.add(clientUiLabel);
        domibusConnector.getStyle().set("text-align", "center");

        var europaLogo = new Div();
        var europa = new Image("frontend/images/europa-logo.jpg", "europe");
        europa.setHeight(ViewConstant.LENGTH_50_PX);
        europaLogo.add(europa);
        europaLogo.setHeight(ViewConstant.LENGTH_50_PX);

        add(ecodexLogo, domibusConnector, europaLogo);
        setAlignItems(Alignment.CENTER);
        expand(domibusConnector);
        setJustifyContentMode(
            com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER);
        setWidth("95%");
    }
}
