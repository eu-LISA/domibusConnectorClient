package eu.domibus.connector.client.impl;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import eu.domibus.connector.client.DomibusConnectorClientMessageBuilder;
import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;

@Component
public class DomibusConnectorClientMessageBuilderImpl implements DomibusConnectorClientMessageBuilder {

	
	@Override
	public DomibusConnectorMessageType createNewMessage(String backendMessageId,
			String ebmsMessageId,
			String conversationId, String businessService,
			String businessServiceType, String businessAction, String fromPartyId, String fromPartyIdType,
			String fromPartyRole, String toPartyId, String toPartyIdType, String toPartyRole, 
			String finalRecipient,
			String originalSender) {
		DomibusConnectorMessageType newMessage = new DomibusConnectorMessageType();
		
		DomibusConnectorMessageDetailsType msgDetails = new DomibusConnectorMessageDetailsType();
		msgDetails.setBackendMessageId(backendMessageId);
		msgDetails.setEbmsMessageId(ebmsMessageId);
		msgDetails.setConversationId(conversationId);
		
		if (StringUtils.hasText(businessService)) {
		DomibusConnectorServiceType service = new DomibusConnectorServiceType();
		service.setService(businessService);
		service.setServiceType(businessServiceType);
		msgDetails.setService(service);
		}
		
		if (StringUtils.hasText(businessAction)) {
		DomibusConnectorActionType action = new DomibusConnectorActionType();
		action.setAction(businessAction);
		msgDetails.setAction(action);
		}
		
		DomibusConnectorPartyType fromParty = new DomibusConnectorPartyType();
		fromParty.setPartyId(fromPartyId);
		fromParty.setPartyIdType(fromPartyIdType);
		fromParty.setRole(fromPartyRole);
		msgDetails.setFromParty(fromParty);		
		
		DomibusConnectorPartyType toParty = new DomibusConnectorPartyType();
		toParty.setPartyId(toPartyId);
		toParty.setPartyIdType(toPartyIdType);
		toParty.setRole(toPartyRole);
		msgDetails.setToParty(toParty);
		
		msgDetails.setFinalRecipient(finalRecipient);
		msgDetails.setOriginalSender(originalSender);
		
		newMessage.setMessageDetails(msgDetails);
		
		return newMessage;
	}
	
	
	@Override
	public DomibusConnectorMessageType createNewConfirmationMessage(String ebmsMessageId,
			String conversationId, DomibusConnectorServiceType businessService,
			String businessAction, DomibusConnectorPartyType fromParty, 
			DomibusConnectorPartyType toParty, 
			String finalRecipient,
			String originalSender, 
			DomibusConnectorConfirmationType confirmationType) {
		DomibusConnectorMessageType newMessage = createNewMessage(
				null, 
				null, 
				conversationId, 
				null, 
				null, 
				businessAction, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				finalRecipient, 
				originalSender);
				
		newMessage.getMessageDetails().setRefToMessageId(ebmsMessageId);
		newMessage.getMessageDetails().setService(businessService);
		newMessage.getMessageDetails().setFromParty(fromParty);
		newMessage.getMessageDetails().setToParty(toParty);
		
		DomibusConnectorMessageConfirmationType confirmation = new DomibusConnectorMessageConfirmationType();
		confirmation.setConfirmationType(confirmationType);
		
		newMessage.getMessageConfirmations().add(confirmation);
		
		return newMessage;
	}

	@Override
	public DomibusConnectorMessageType addBusinessContentXMLAsBinary(DomibusConnectorMessageType message,
			byte[] businessContent) {
		
		StreamSource streamSource = new StreamSource(new ByteArrayInputStream(businessContent));
		
		return addBusinessContentXMLAsStream(message, streamSource);
	}

	@Override
	public DomibusConnectorMessageType addBusinessContentXMLAsStream(DomibusConnectorMessageType message,
			Source businessContent) {
		
		if(message.getMessageContent()==null) {
			message.setMessageContent(new DomibusConnectorMessageContentType());
		}
		
		message.getMessageContent().setXmlContent(businessContent);
		
		return message;
	}

	@Override
	public DomibusConnectorMessageType addBusinessDocumentAsBinary(DomibusConnectorMessageType message,
			byte[] businessDocument, String businessDocumentName) {
		
		DataHandler businessDocumentDh = createDataHandler(businessDocument, "application/octet-stream");
		
		return addBusinessDocumentAsStream(message, businessDocumentDh, businessDocumentName);
	}

	@Override
	public DomibusConnectorMessageType addBusinessDocumentAsStream(DomibusConnectorMessageType message,
			DataHandler businessDocument, String businessDocumentName) {
		
		if(message.getMessageContent()==null) {
			message.setMessageContent(new DomibusConnectorMessageContentType());
		}
		
		DomibusConnectorMessageDocumentType document = new DomibusConnectorMessageDocumentType();
		
		document.setDocumentName(businessDocumentName);
		document.setDocument(businessDocument);
		
		message.getMessageContent().setDocument(document );
		
		return message;
	}

	@Override
	public DomibusConnectorMessageType addDetachedSignatureForBusinessDocument(DomibusConnectorMessageType message,
			byte[] detachedSignature, String detachedSignatureName,
			DomibusConnectorDetachedSignatureMimeType detachedSignatureType) {
		if(message!=null && message.getMessageContent()!=null && message.getMessageContent().getDocument()!=null) {
			
			DomibusConnectorDetachedSignatureType dS = new DomibusConnectorDetachedSignatureType();

			dS.setDetachedSignature(detachedSignature);
			dS.setDetachedSignatureName(detachedSignatureName);
			dS.setMimeType(detachedSignatureType);
			
			message.getMessageContent().getDocument().setDetachedSignature(dS );
			return message;
		}
		return null;
	}

	@Override
	public DomibusConnectorMessageType addBusinessAttachmentAsBinaryToMessage(DomibusConnectorMessageType message,
			String businessAttachmentIdentifier, byte[] businessAttachment, String businessAttachmentName,
			String businessAttachmentMimeType, String businessAttachmentDescription) {
		
		DataHandler businessAttachmentDh = createDataHandler(businessAttachment, "application/octet-stream");
		
		return addBusinessAttachmentAsStreamToMessage(message, businessAttachmentIdentifier, businessAttachmentDh, businessAttachmentName, businessAttachmentMimeType, businessAttachmentDescription);
	}

	@Override
	public DomibusConnectorMessageType addBusinessAttachmentAsStreamToMessage(DomibusConnectorMessageType message,
			String businessAttachmentIdentifier, DataHandler businessAttachment, String businessAttachmentName,
			String businessAttachmentMimeType, String businessAttachmentDescription) {
		
		DomibusConnectorMessageAttachmentType attachment = new DomibusConnectorMessageAttachmentType();
		attachment.setAttachment(businessAttachment);
		attachment.setDescription(businessAttachmentDescription);
		attachment.setIdentifier(businessAttachmentIdentifier);
		attachment.setMimeType(businessAttachmentMimeType);
		attachment.setName(businessAttachmentName);
		
		message.getMessageAttachments().add(attachment);
		
		return message;
	}

	private DataHandler createDataHandler(byte[] content, String type) {
        DataSource ds = new ByteArrayDataSource(content, type);
        DataHandler dh = new DataHandler(ds);

        return dh;
    }
	

}
