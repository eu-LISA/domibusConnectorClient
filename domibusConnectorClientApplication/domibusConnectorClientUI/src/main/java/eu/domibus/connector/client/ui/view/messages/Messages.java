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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.client.ui.ViewConstant;
import eu.domibus.connector.client.ui.component.LumoLabel;
import eu.domibus.connector.client.ui.view.DomibusConnectorClientUIMainView;
import lombok.Data;

/**
 * The Messages class is responsible for managing and displaying a set of message-related views in
 * the Domibus connector client UI.
 */
@Data
@UIScope
@ParentLayout(DomibusConnectorClientUIMainView.class)
@RoutePrefix(Messages.ROUTE_PREFIX)
@Route(value = Messages.ROUTE, layout = DomibusConnectorClientUIMainView.class)
@org.springframework.stereotype.Component
public class Messages extends VerticalLayout implements RouterLayout, BeforeEnterObserver {
    private static final long serialVersionUID = 1L;
    public static final String ROUTE = "messages";
    public static final String ROUTE_PREFIX = "messages";
    Tabs messagesMenu = new Tabs();
    Tab messagesListTab;
    Tab messageDetailsTab;
    Tab sendMessageTab;
    MessagesList messagesListView;
    MessageDetails messageDetailsView;
    SendMessage sendMessageView;

    /**
     * The Messages class represents a component that displays different tabs for managing and
     * interacting with messages.
     */
    public Messages() {
        messagesListTab = new Tab(
            createRouterLink(
                "Messages List",
                new Icon(VaadinIcon.ENVELOPES_O),
                MessagesList.class
            )
        );

        messageDetailsTab = new Tab(
            createRouterLink(
                "Message Details",
                new Icon(VaadinIcon.ENVELOPE_OPEN_O),
                MessageDetails.class
            ));

        sendMessageTab = new Tab(
            createRouterLink("Send Message", new Icon(VaadinIcon.ENVELOPE), SendMessage.class)
        );

        messagesMenu.add(messagesListTab, messageDetailsTab, sendMessageTab);
        messagesMenu.setOrientation(Tabs.Orientation.HORIZONTAL);
        messagesMenu.addSelectedChangeListener(e -> {
            e.getPreviousTab().setSelected(false);
            e.getSelectedTab().setSelected(true);
        });

        add(messagesMenu);
    }

    /**
     * Shows the details of a specific message.
     *
     * @param connectorMessageId the ID of the message to display details for
     */
    public void showMessageDetails(Long connectorMessageId) {
        messagesMenu.setSelectedTab(messageDetailsTab);

        UI.getCurrent()
          .navigate(Messages.ROUTE_PREFIX + "/" + MessageDetails.ROUTE + "/" + connectorMessageId);
    }

    /**
     * Displays the send message tab and navigates to the send message view with the specified
     * connector message ID.
     *
     * @param connectorMessageId the ID of the message to send (type: Long)
     */
    public void showSendMessage(Long connectorMessageId) {
        messagesMenu.setSelectedTab(sendMessageTab);
        UI.getCurrent()
          .navigate(Messages.ROUTE_PREFIX + "/" + SendMessage.ROUTE + "/" + connectorMessageId);
    }

    /**
     * Sets the message list as the selected tab in the messages menu and navigates to the message
     * list view.
     */
    public void showMessagesList() {
        messagesMenu.setSelectedTab(messagesListTab);
        UI.getCurrent().navigate(MessagesList.class);
    }

    private RouterLink createRouterLink(
        String tabLabel, Icon tabIcon, Class<? extends Component> component) {
        var tabText = new Span(tabLabel);
        tabText.getStyle().set("font-size", "15px");

        tabIcon.setSize("15px");

        var tabLayout = new HorizontalLayout(tabIcon, tabText);
        tabLayout.setAlignItems(Alignment.CENTER);

        var routerLink = new RouterLink(null, component);
        routerLink.add(tabLayout);

        return routerLink;
    }

    /**
     * Retrieves a dialog for confirming the deletion of a message. The dialog contains a header
     * with the text "Delete message" displayed in bold and italic font, and a label with the
     * message "Are you sure you want to delete that message? All database references and storage
     * files (if available) are deleted as well!".
     *
     * @return a {@link Dialog} object representing the delete message confirmation dialog
     */
    public Dialog getDeleteMessageDialog() {
        var headerContent = new Div();
        var header = new Label("Delete message");
        header.getStyle().set("font-weight", "bold");
        header.getStyle().set("font-style", "italic");
        headerContent.getStyle().set("text-align", "center");
        headerContent.getStyle().set("padding", ViewConstant.LENGTH_10_PX);
        headerContent.add(header);

        var dialog = new Dialog();
        dialog.add(headerContent);

        var labelContent = new Div();
        var label = new LumoLabel(
            "Are you sure you want to delete that message? All database references and "
                + "storage files (if available) are deleted as well!"
        );

        labelContent.add(label);
        dialog.add(labelContent);

        return dialog;
    }

    /**
     * Retrieves a dialog that displays an error message.
     *
     * @param header  the header of the dialog
     * @param message the error message to be displayed
     * @return a Dialog object representing the error dialog
     */
    public Dialog getErrorDialog(String header, String message) {
        var headerContent = new Div();
        var headerLabel = new Label(header);
        headerLabel.getStyle().set("font-weight", "bold");
        headerLabel.getStyle().set("font-style", "italic");
        headerContent.getStyle().set("text-align", "center");
        headerContent.getStyle().set("padding", ViewConstant.LENGTH_10_PX);
        headerContent.add(headerLabel);

        var dialog = new Dialog();
        dialog.add(headerContent);

        var labelContent = new Div();
        var label = new LumoLabel(message);

        labelContent.add(label);
        dialog.add(labelContent);

        return dialog;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent arg0) {
        if (arg0.getNavigationTarget().equals(Messages.class)) {
            if (messagesListTab.isSelected()) {
                arg0.forwardTo(MessagesList.class);
                UI.getCurrent().navigate(MessagesList.class);
            } else if (messageDetailsTab.isSelected()) {
                arg0.forwardTo(MessageDetails.class);
                UI.getCurrent().navigate(MessageDetails.class);
            } else if (sendMessageTab.isSelected()) {
                arg0.forwardTo(SendMessage.class);
                UI.getCurrent().navigate(SendMessage.class);
            }
        }
    }
}
