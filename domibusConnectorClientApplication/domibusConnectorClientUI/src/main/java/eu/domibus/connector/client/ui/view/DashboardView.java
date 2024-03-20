package eu.domibus.connector.client.ui.view;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;


@UIScope
@Route(value = DashboardView.ROUTE, layout = DomibusConnectorClientUIMainView.class)
@PageTitle("domibusConnectorClient - Administrator")
@Component
public class DashboardView extends VerticalLayout
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String ROUTE = "";

	Label l = new Label();
	
    public DashboardView() {
    	l.setText("Welcome to domibusConnectorClient Administration UI");
		add(l);
    }


}
