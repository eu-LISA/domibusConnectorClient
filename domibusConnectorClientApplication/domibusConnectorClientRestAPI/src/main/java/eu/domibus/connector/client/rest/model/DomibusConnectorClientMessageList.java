package eu.domibus.connector.client.rest.model;

import java.util.ArrayList;
import java.util.List;

public class DomibusConnectorClientMessageList {
	
	private List<DomibusConnectorClientMessage> messages;

	public DomibusConnectorClientMessageList() {
		setMessages(new ArrayList<DomibusConnectorClientMessage>());
	}

	public List<DomibusConnectorClientMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<DomibusConnectorClientMessage> messages) {
		this.messages = messages;
	}

}
