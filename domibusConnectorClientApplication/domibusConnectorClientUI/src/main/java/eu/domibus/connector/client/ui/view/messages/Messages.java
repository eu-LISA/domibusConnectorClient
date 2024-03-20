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

import eu.domibus.connector.client.ui.component.LumoLabel;
import eu.domibus.connector.client.ui.view.DomibusConnectorClientUIMainView;


@UIScope
@ParentLayout(DomibusConnectorClientUIMainView.class)
@RoutePrefix(Messages.ROUTE_PREFIX)
@Route(value = Messages.ROUTE, layout = DomibusConnectorClientUIMainView.class)
@org.springframework.stereotype.Component
public class Messages extends VerticalLayout implements RouterLayout, BeforeEnterObserver
{
	
	/**
	 * 
	 */
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
	
	public Messages()  {
		
		messagesListTab = new Tab(createRouterLink("Messages List", new Icon(VaadinIcon.ENVELOPES_O), MessagesList.class));
		
		messageDetailsTab = new Tab(createRouterLink("Message Details", new Icon(VaadinIcon.ENVELOPE_OPEN_O), MessageDetails.class));
		
		sendMessageTab = new Tab(createRouterLink("Send Message", new Icon(VaadinIcon.ENVELOPE), SendMessage.class));
		
		messagesMenu.add(messagesListTab, messageDetailsTab, sendMessageTab);
		
		messagesMenu.setOrientation(Tabs.Orientation.HORIZONTAL);
		
		messagesMenu.addSelectedChangeListener(e -> {
			e.getPreviousTab().setSelected(false);
			e.getSelectedTab().setSelected(true);
		});

        add(messagesMenu);
	}
	
	public void showMessageDetails(Long connectorMessageId) {
		messagesMenu.setSelectedTab(messageDetailsTab);
		
		UI.getCurrent().navigate(Messages.ROUTE_PREFIX +"/"+ MessageDetails.ROUTE +"/"+ connectorMessageId);
	}
	
	public void showSendMessage(Long connectorMessageId) {
		messagesMenu.setSelectedTab(sendMessageTab);

		UI.getCurrent().navigate(Messages.ROUTE_PREFIX +"/"+ SendMessage.ROUTE +"/"+ connectorMessageId);
	}
	
	public void showMessagesList() {
		messagesMenu.setSelectedTab(messagesListTab);

		UI.getCurrent().navigate(MessagesList.class);
	}
	
	private RouterLink createRouterLink(String tabLabel, Icon tabIcon, Class<? extends Component> component) {
		Span tabText = new Span(tabLabel);
		tabText.getStyle().set("font-size", "15px");
		
		tabIcon.setSize("15px");
		
		HorizontalLayout tabLayout = new HorizontalLayout(tabIcon, tabText);
		tabLayout.setAlignItems(Alignment.CENTER);
		
		RouterLink routerLink = new RouterLink(null, component);
		routerLink.add(tabLayout);
		
		return routerLink;
	}
	
	public Dialog getDeleteMessageDialog() {
		Dialog diag = new Dialog();
			
			Div headerContent = new Div();
			Label header = new Label("Delete message");
			header.getStyle().set("font-weight", "bold");
			header.getStyle().set("font-style", "italic");
			headerContent.getStyle().set("text-align", "center");
			headerContent.getStyle().set("padding", "10px");
			headerContent.add(header);
			diag.add(headerContent);

			Div labelContent = new Div();
			LumoLabel label = new LumoLabel("Are you sure you want to delete that message? All database references and storage files (if available) are deleted as well!");
			
			labelContent.add(label);
			diag.add(labelContent);
			
			
			return diag;
		
	}
	
	public Dialog getErrorDialog(String header, String message) {
		Dialog diag = new Dialog();
		
		Div headerContent = new Div();
		Label headerLabel = new Label(header);
		headerLabel.getStyle().set("font-weight", "bold");
		headerLabel.getStyle().set("font-style", "italic");
		headerContent.getStyle().set("text-align", "center");
		headerContent.getStyle().set("padding", "10px");
		headerContent.add(headerLabel);
		diag.add(headerContent);

		Div labelContent = new Div();
		LumoLabel label = new LumoLabel(message);
		
		labelContent.add(label);
		diag.add(labelContent);
		
		return diag;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent arg0) {
		if(arg0.getNavigationTarget().equals(Messages.class)) {
		if(messagesListTab.isSelected()) {
			arg0.forwardTo(MessagesList.class);
			UI.getCurrent().navigate(MessagesList.class);
		}else if (messageDetailsTab.isSelected()) {
			arg0.forwardTo(MessageDetails.class);
			UI.getCurrent().navigate(MessageDetails.class);
		}else if (sendMessageTab.isSelected()) {
			arg0.forwardTo(SendMessage.class);
			UI.getCurrent().navigate(SendMessage.class);
		}
		}
		
	}

	public MessagesList getMessagesListView() {
		return messagesListView;
	}

	public void setMessagesListView(MessagesList messagesListView) {
		this.messagesListView = messagesListView;
	}

	public MessageDetails getMessageDetailsView() {
		return messageDetailsView;
	}

	public void setMessageDetailsView(MessageDetails messageDetailsView) {
		this.messageDetailsView = messageDetailsView;
	}

	public SendMessage getSendMessageView() {
		return sendMessageView;
	}

	public void setSendMessageView(SendMessage sendMessageView) {
		this.sendMessageView = sendMessageView;
	}



	

}
