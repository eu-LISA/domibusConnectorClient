/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.filesystem.isupport.sbdh.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Data;

/**
 * The StandardBusinessDocumentHeader class represents the header of a standard business document.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "", propOrder = {
    "headerVersion",
    "transport",
    "message"
}
)
@XmlRootElement(name = "StandardBusinessDocumentHeader")
@Data
@SuppressWarnings("checkstyle:LineLength")
public class StandardBusinessDocumentHeader {
    @XmlElement(name = "HeaderVersion")
    protected float headerVersion;
    @XmlElement(required = true)
    protected StandardBusinessDocumentHeader.Transport transport;
    @XmlElement(required = true)
    protected StandardBusinessDocumentHeader.Message message;

    /**
     * Classe Java per anonymous complex type.
     *
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa
     * classe.
     *
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Documents"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="DocumentIdentification" maxOccurs="unbounded" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="UniformResourceIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                             &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                             &lt;element name="MimeTypeQualifierCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                             &lt;element name="fileSize" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
     *                             &lt;element name="fileCreationDateAndTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
     *                             &lt;element name="fileHashCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
        name = "", propOrder = {
        "documents"
    }
    )
    @Data
    public static class Message {
        @XmlElement(name = "Documents", required = true)
        protected StandardBusinessDocumentHeader.Message.Documents documents;

        /**
         * Classe Java per anonymous complex type.
         *
         * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa
         * classe.
         *
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;sequence&gt;
         *         &lt;element name="DocumentIdentification" maxOccurs="unbounded" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="UniformResourceIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                   &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                   &lt;element name="MimeTypeQualifierCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                   &lt;element name="fileSize" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
         *                   &lt;element name="fileCreationDateAndTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
         *                   &lt;element name="fileHashCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *       &lt;/sequence&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(
            name = "", propOrder = {
            "documentIdentification"
        }
        )
        public static class Documents {
            @XmlElement(name = "DocumentIdentification")
            protected List<StandardBusinessDocumentHeader.Message.Documents.DocumentIdentification>
                documentIdentification;

            /**
             * Gets the value of the documentIdentification property.
             *
             * <p>This accessor method returns a reference to the live list, not a snapshot.
             * Therefore, any modification you make to the returned list will be present inside the
             * JAXB object. This is why there is not a <CODE>set</CODE>; method for the
             * documentIdentification property.
             *
             * <p>For example, to add a new item, do as follows:
             * <pre>
             *    getDocumentIdentification().add(newItem);
             * </pre>
             *
             *
             * <p>Objects of the following type(s) are allowed in the list
             * {@link StandardBusinessDocumentHeader.Message.Documents.DocumentIdentification }
             *
             * @return documentIdentification
             */
            public List<StandardBusinessDocumentHeader.Message.Documents.DocumentIdentification>
            getDocumentIdentification() {
                if (documentIdentification == null) {
                    documentIdentification = new ArrayList<>();
                }
                return this.documentIdentification;
            }

            /**
             * Classe Java per anonymous complex type.
             *
             * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in
             * questa classe.
             *
             * <pre>
             * &lt;complexType&gt;
             *   &lt;complexContent&gt;
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *       &lt;sequence&gt;
             *         &lt;element name="UniformResourceIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *         &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *         &lt;element name="MimeTypeQualifierCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *         &lt;element name="fileSize" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
             *         &lt;element name="fileCreationDateAndTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
             *         &lt;element name="fileHashCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *       &lt;/sequence&gt;
             *     &lt;/restriction&gt;
             *   &lt;/complexContent&gt;
             * &lt;/complexType&gt;
             * </pre>
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(
                name = "", propOrder = {
                "uniformResourceIdentifier",
                "type",
                "mimeTypeQualifierCode",
                "fileSize",
                "fileCreationDateAndTime",
                "fileHashCode"
            }
            )
            @Data
            public static class DocumentIdentification {
                @XmlElement(name = "UniformResourceIdentifier", required = true)
                protected String uniformResourceIdentifier;
                @XmlElement(name = "Type", required = true)
                protected String type;
                @XmlElement(name = "MimeTypeQualifierCode", required = true)
                protected String mimeTypeQualifierCode;
                protected long fileSize;
                @XmlElement(required = true)
                @XmlSchemaType(name = "dateTime")
                protected XMLGregorianCalendar fileCreationDateAndTime;
                @XmlElement(required = true)
                protected String fileHashCode;
            }
        }
    }

    /**
     * Classe Java per anonymous complex type.
     *
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa
     * classe.
     *
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Sender"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="Identifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                   &lt;element name="ContactInformation"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Contact" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                             &lt;element name="EmailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                             &lt;element name="FaxNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                             &lt;element name="TelephoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                             &lt;element name="ContactTypeIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="Receiver"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="Identifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                   &lt;element name="ContactInformation"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="Contact" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                             &lt;element name="EmailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                             &lt;element name="FaxNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                             &lt;element name="TelephoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                             &lt;element name="ContactTypeIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="msgCreationDateAndTime" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
     *         &lt;element name="msgId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="msgHashCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
        name = "", propOrder = {
        "sender",
        "receiver",
        "msgCreationDateAndTime",
        "msgId",
        "caseId",
        "msgHashCode"
    }
    )
    @Data
    public static class Transport {
        @XmlElement(name = "Sender", required = true)
        protected StandardBusinessDocumentHeader.Transport.Sender sender;
        @XmlElement(name = "Receiver", required = true)
        protected StandardBusinessDocumentHeader.Transport.Receiver receiver;
        protected long msgCreationDateAndTime;
        @XmlElement(required = true)
        protected String msgId;
        @XmlElement(required = false)
        protected String caseId;
        @XmlElement(required = true)
        protected String msgHashCode;

        /**
         * Classe Java per anonymous complex type.
         *
         * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa
         * classe.
         *
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;sequence&gt;
         *         &lt;element name="Identifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *         &lt;element name="ContactInformation"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Contact" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                   &lt;element name="EmailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                   &lt;element name="FaxNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                   &lt;element name="TelephoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                   &lt;element name="ContactTypeIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *       &lt;/sequence&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(
            name = "", propOrder = {
            "identifier",
            "contactInformation"
        }
        )
        @Data
        public static class Receiver {
            @XmlElement(name = "Identifier", required = true)
            protected String identifier;
            @XmlElement(name = "contactInformation", required = true)
            protected StandardBusinessDocumentHeader.Transport.Receiver.ContactInformation
                contactInformation;

            /**
             * Classe Java per anonymous complex type.
             *
             * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in
             * questa classe.
             *
             * <pre>
             * &lt;complexType&gt;
             *   &lt;complexContent&gt;
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *       &lt;sequence&gt;
             *         &lt;element name="Contact" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *         &lt;element name="EmailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *         &lt;element name="FaxNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *         &lt;element name="TelephoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *         &lt;element name="ContactTypeIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *       &lt;/sequence&gt;
             *     &lt;/restriction&gt;
             *   &lt;/complexContent&gt;
             * &lt;/complexType&gt;
             * </pre>
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(
                name = "", propOrder = {
                "contact",
                "emailAddress",
                "faxNumber",
                "telephoneNumber",
                "contactTypeIdentifier"
            }
            )
            @Data
            public static class ContactInformation {
                @XmlElement(name = "Contact", required = true)
                protected String contact;
                @XmlElement(name = "EmailAddress", required = true)
                protected String emailAddress;
                @XmlElement(name = "FaxNumber", required = true)
                protected String faxNumber;
                @XmlElement(name = "TelephoneNumber", required = true)
                protected String telephoneNumber;
                @XmlElement(name = "ContactTypeIdentifier", required = true)
                protected String contactTypeIdentifier;
            }
        }

        /**
         * Classe Java per anonymous complex type.
         *
         * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa
         * classe.
         *
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;sequence&gt;
         *         &lt;element name="Identifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *         &lt;element name="ContactInformation"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="Contact" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                   &lt;element name="EmailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                   &lt;element name="FaxNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                   &lt;element name="TelephoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                   &lt;element name="ContactTypeIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *       &lt;/sequence&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(
            name = "", propOrder = {
            "identifier",
            "contactInformation"
        }
        )
        @Data
        public static class Sender {
            @XmlElement(name = "Identifier", required = true)
            protected String identifier;
            @XmlElement(name = "contactInformation", required = true)
            protected StandardBusinessDocumentHeader.Transport.Sender.ContactInformation
                contactInformation;

            /**
             * Classe Java per anonymous complex type.
             *
             * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in
             * questa classe.
             *
             * <pre>
             * &lt;complexType&gt;
             *   &lt;complexContent&gt;
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *       &lt;sequence&gt;
             *         &lt;element name="Contact" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *         &lt;element name="EmailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *         &lt;element name="FaxNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *         &lt;element name="TelephoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *         &lt;element name="ContactTypeIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
             *       &lt;/sequence&gt;
             *     &lt;/restriction&gt;
             *   &lt;/complexContent&gt;
             * &lt;/complexType&gt;
             * </pre>
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(
                name = "", propOrder = {
                "contact",
                "emailAddress",
                "faxNumber",
                "telephoneNumber",
                "contactTypeIdentifier"
            }
            )
            @Data
            public static class ContactInformation {
                @XmlElement(name = "Contact", required = true)
                protected String contact;
                @XmlElement(name = "EmailAddress", required = true)
                protected String emailAddress;
                @XmlElement(name = "FaxNumber", required = true)
                protected String faxNumber;
                @XmlElement(name = "TelephoneNumber", required = true)
                protected String telephoneNumber;
                @XmlElement(name = "ContactTypeIdentifier", required = true)
                protected String contactTypeIdentifier;
            }
        }
    }
}
