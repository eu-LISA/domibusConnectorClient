package eu.domibus.connector.client.ui.view.configuration;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.client.ui.component.LumoLabel;
import eu.domibus.connector.client.ui.view.DomibusConnectorClientUIMainView;

@UIScope
@ParentLayout(DomibusConnectorClientUIMainView.class)
@RoutePrefix(Configuration.ROUTE_PREFIX)
@Route(value = Configuration.ROUTE, layout = DomibusConnectorClientUIMainView.class)
@org.springframework.stereotype.Component
public class Configuration extends VerticalLayout implements RouterLayout, BeforeEnterObserver{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String ROUTE = "configuration";
	
	public static final String ROUTE_PREFIX = "configuration";
	
	public Configuration() {
		LumoLabel notYet = new LumoLabel("Not implemented yet!");
		
		add(notYet);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
