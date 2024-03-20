package eu.domibus.connector.client.ui.form;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class FormsUtil {

	
	public static TextField getFormattedTextFieldReadOnly() {
		TextField loc = new TextField();
		loc.setReadOnly(true);
		loc.getStyle().set("fontSize", "13px");
		loc.setWidth("600px");
		return loc;
	}
	
	public static TextField getFormattedTextField() {
		TextField loc = new TextField();
//		loc.setReadOnly(true);
		loc.getStyle().set("fontSize", "13px");
		loc.setWidth("600px");
		return loc;
	}
	
	public static TextField getFormattedRequiredTextField() {
		TextField loc = getFormattedTextField();
		loc.setRequired(true);
		return loc;
	}

	public static TextArea getFormattedTextArea() {
		TextArea loc = new TextArea();
		loc.getStyle().set("fontSize", "13px");
		loc.setWidth("600px");
		return loc;
	}

}
