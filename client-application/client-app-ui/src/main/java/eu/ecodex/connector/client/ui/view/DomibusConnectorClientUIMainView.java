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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.client.ui.ViewConstant;
import eu.ecodex.connector.client.ui.view.configuration.Configuration;
import eu.ecodex.connector.client.ui.view.messages.Messages;
import java.util.Optional;

/**
 * The DomibusConnectorClientUIMainView class is a component that represents the main view of the
 * Domibus Connector Client UI application.
 */
@UIScope
@org.springframework.stereotype.Component
public class DomibusConnectorClientUIMainView extends AppLayout implements RouterLayout {
    private static final long serialVersionUID = 1L;
    Tabs mainMenu = new Tabs();
    Tab messagesTab;
    Tab configurationTab;

    /**
     * Constructor.
     */
    public DomibusConnectorClientUIMainView() {
        setPrimarySection(Section.NAVBAR);

        var topBarLayout = new VerticalLayout();
        topBarLayout.add(new DomibusConnectorClientUIHeader());
        addToNavbar(topBarLayout);

        messagesTab =
            new Tab(createRouterLink("Messages", new Icon(VaadinIcon.ENVELOPES), Messages.class));
        messagesTab.setSelected(false);

        configurationTab = new Tab(
            createRouterLink("Configuration", new Icon(VaadinIcon.COGS), Configuration.class));
        configurationTab.setSelected(false);

        mainMenu.add(messagesTab, configurationTab);

        mainMenu.setOrientation(Tabs.Orientation.HORIZONTAL);

        mainMenu.addSelectedChangeListener(e -> {
            e.getPreviousTab().setSelected(false);
            e.getSelectedTab().setSelected(true);
        });

        topBarLayout.add(mainMenu);
    }

    private RouterLink createRouterLink(
        String tabLabel, Icon tabIcon, Class<? extends Component> component) {
        var tabText = new Span(tabLabel);
        tabText.getStyle().set("font-size", ViewConstant.LENGTH_20_PX);

        tabIcon.setSize(ViewConstant.LENGTH_20_PX);

        var tabLayout = new HorizontalLayout(tabIcon, tabText);
        tabLayout.setAlignItems(Alignment.CENTER);

        var routerLink = new RouterLink((String) null, component);
        routerLink.add(tabLayout);

        return routerLink;
    }
}
