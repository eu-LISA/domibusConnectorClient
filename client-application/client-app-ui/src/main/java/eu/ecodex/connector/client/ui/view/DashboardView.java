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

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

/**
 * The DashboardView class represents the dashboard page of the domibusConnectorClient
 * Administration UI.
 */
@UIScope
@Route(value = DashboardView.ROUTE, layout = DomibusConnectorClientUIMainView.class)
@PageTitle("domibusConnectorClient - Administrator")
@Component
public class DashboardView extends VerticalLayout {
    private static final long serialVersionUID = 1L;
    public static final String ROUTE = "";
    Label label = new Label();

    public DashboardView() {
        label.setText("Welcome to domibusConnectorClient Administration UI");
        add(label);
    }
}
