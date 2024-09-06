/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.ui.view.configuration;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.client.ui.component.LumoLabel;
import eu.ecodex.connector.client.ui.view.DomibusConnectorClientUIMainView;

/**
 * The {@code Configuration} class represents a UI component for the Configuration page in the
 * Domibus Connector Client Application.
 */
@UIScope
@ParentLayout(DomibusConnectorClientUIMainView.class)
@RoutePrefix(Configuration.ROUTE_PREFIX)
@Route(value = Configuration.ROUTE, layout = DomibusConnectorClientUIMainView.class)
@org.springframework.stereotype.Component
@SuppressWarnings("squid:S1135")
public class Configuration extends VerticalLayout implements RouterLayout, BeforeEnterObserver {
    private static final long serialVersionUID = 1L;
    public static final String ROUTE = "configuration";
    public static final String ROUTE_PREFIX = "configuration";

    public Configuration() {
        var notYet = new LumoLabel("Not implemented yet!");
        add(notYet);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent arg0) {
        // TODO Auto-generated method stub
    }
}
