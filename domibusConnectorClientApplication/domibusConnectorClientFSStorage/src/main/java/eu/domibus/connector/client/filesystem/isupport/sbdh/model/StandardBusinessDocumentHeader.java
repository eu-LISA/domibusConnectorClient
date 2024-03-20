package eu.domibus.connector.client.filesystem.isupport.sbdh.model;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "headerVersion",
        "transport",
        "message"
})
@XmlRootElement(name = "StandardBusinessDocumentHeader")
public class StandardBusinessDocumentHeader {

    @XmlElement(name = "HeaderVersion")
    protected float headerVersion;
    @XmlElement(required = true)
    protected StandardBusinessDocumentHeader.Transport transport;
    @XmlElement(required = true)
    protected StandardBusinessDocumentHeader.Message message;

    /**
     * Recupera il valore della proprietà headerVersion.
     * @return headerVersion
     */
    public float getHeaderVersion() {
        return headerVersion;
    }

    /**
     * Imposta il valore della proprietà headerVersion.
     * @param value value
     */
    public void setHeaderVersion(float value) {
        this.headerVersion = value;
    }

    /**
     * Recupera il valore della proprietà transport.
     *
     * @return
     *     possible object is
     *     {@link StandardBusinessDocumentHeader.Transport }
     *
     */
    public StandardBusinessDocumentHeader.Transport getTransport() {
        return transport;
    }

    /**
     * Imposta il valore della proprietà transport.
     *
     * @param value
     *     allowed object is
     *     {@link StandardBusinessDocumentHeader.Transport }
     *
     */
    public void setTransport(StandardBusinessDocumentHeader.Transport value) {
        this.transport = value;
    }

    /**
     * Recupera il valore della proprietà message.
     *
     * @return
     *     possible object is
     *     {@link StandardBusinessDocumentHeader.Message }
     *
     */
    public StandardBusinessDocumentHeader.Message getMessage() {
        return message;
    }

    /**
     * Imposta il valore della proprietà message.
     *
     * @param value
     *     allowed object is
     *     {@link StandardBusinessDocumentHeader.Message }
     *
     */
    public void setMessage(StandardBusinessDocumentHeader.Message value) {
        this.message = value;
    }


    /**
     * <p>Classe Java per anonymous complex type.
     *
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
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
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "documents"
    })
    public static class Message {

        @XmlElement(name = "Documents", required = true)
        protected StandardBusinessDocumentHeader.Message.Documents documents;

        /**
         * Recupera il valore della proprietà documents.
         *
         * @return
         *     possible object is
         *     {@link StandardBusinessDocumentHeader.Message.Documents }
         *
         */
        public StandardBusinessDocumentHeader.Message.Documents getDocuments() {
            return documents;
        }

        /**
         * Imposta il valore della proprietà documents.
         *
         * @param value
         *     allowed object is
         *     {@link StandardBusinessDocumentHeader.Message.Documents }
         *
         */
        public void setDocuments(StandardBusinessDocumentHeader.Message.Documents value) {
            this.documents = value;
        }


        /**
         * <p>Classe Java per anonymous complex type.
         *
         * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
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
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "documentIdentification"
        })
        public static class Documents {

            @XmlElement(name = "DocumentIdentification")
            protected List<StandardBusinessDocumentHeader.Message.Documents.DocumentIdentification> documentIdentification;

            /**
             * Gets the value of the documentIdentification property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE>; method for the documentIdentification property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getDocumentIdentification().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link StandardBusinessDocumentHeader.Message.Documents.DocumentIdentification }
             *
             * @return documentIdentification
             */
            public List<StandardBusinessDocumentHeader.Message.Documents.DocumentIdentification> getDocumentIdentification() {
                if (documentIdentification == null) {
                    documentIdentification = new ArrayList<StandardBusinessDocumentHeader.Message.Documents.DocumentIdentification>();
                }
                return this.documentIdentification;
            }


            /**
             * <p>Classe Java per anonymous complex type.
             *
             * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
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
             *
             *
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                    "uniformResourceIdentifier",
                    "type",
                    "mimeTypeQualifierCode",
                    "fileSize",
                    "fileCreationDateAndTime",
                    "fileHashCode"
            })
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

                /**
                 * Recupera il valore della proprietà uniformResourceIdentifier.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getUniformResourceIdentifier() {
                    return uniformResourceIdentifier;
                }

                /**
                 * Imposta il valore della proprietà uniformResourceIdentifier.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setUniformResourceIdentifier(String value) {
                    this.uniformResourceIdentifier = value;
                }

                /**
                 * Recupera il valore della proprietà type.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getType() {
                    return type;
                }

                /**
                 * Imposta il valore della proprietà type.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setType(String value) {
                    this.type = value;
                }

                /**
                 * Recupera il valore della proprietà mimeTypeQualifierCode.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getMimeTypeQualifierCode() {
                    return mimeTypeQualifierCode;
                }

                /**
                 * Imposta il valore della proprietà mimeTypeQualifierCode.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setMimeTypeQualifierCode(String value) {
                    this.mimeTypeQualifierCode = value;
                }

                /**
                 * Recupera il valore della proprietà fileSize.
                 * @return file size
                 */
                public long getFileSize() {
                    return fileSize;
                }

                /**
                 * Imposta il valore della proprietà fileSize.
                 * @param value value
                 */
                public void setFileSize(long value) {
                    this.fileSize = value;
                }

                /**
                 * Recupera il valore della proprietà fileCreationDateAndTime.
                 *
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *
                 */
                public XMLGregorianCalendar getFileCreationDateAndTime() {
                    return fileCreationDateAndTime;
                }

                /**
                 * Imposta il valore della proprietà fileCreationDateAndTime.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *
                 */
                public void setFileCreationDateAndTime(XMLGregorianCalendar value) {
                    this.fileCreationDateAndTime = value;
                }

                /**
                 * Recupera il valore della proprietà fileHashCode.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getFileHashCode() {
                    return fileHashCode;
                }

                /**
                 * Imposta il valore della proprietà fileHashCode.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setFileHashCode(String value) {
                    this.fileHashCode = value;
                }

            }

        }

    }


    /**
     * <p>Classe Java per anonymous complex type.
     *
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
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
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "sender",
            "receiver",
            "msgCreationDateAndTime",
            "msgId",
            "caseId",
            "msgHashCode"
    })
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
         * Recupera il valore della proprietà sender.
         *
         * @return
         *     possible object is
         *     {@link StandardBusinessDocumentHeader.Transport.Sender }
         *
         */
        public StandardBusinessDocumentHeader.Transport.Sender getSender() {
            return sender;
        }

        /**
         * Imposta il valore della proprietà sender.
         *
         * @param value
         *     allowed object is
         *     {@link StandardBusinessDocumentHeader.Transport.Sender }
         *
         */
        public void setSender(StandardBusinessDocumentHeader.Transport.Sender value) {
            this.sender = value;
        }

        /**
         * Recupera il valore della proprietà receiver.
         *
         * @return
         *     possible object is
         *     {@link StandardBusinessDocumentHeader.Transport.Receiver }
         *
         */
        public StandardBusinessDocumentHeader.Transport.Receiver getReceiver() {
            return receiver;
        }

        /**
         * Imposta il valore della proprietà receiver.
         *
         * @param value
         *     allowed object is
         *     {@link StandardBusinessDocumentHeader.Transport.Receiver }
         *
         */
        public void setReceiver(StandardBusinessDocumentHeader.Transport.Receiver value) {
            this.receiver = value;
        }

        /**
         * Recupera il valore della proprietà msgCreationDateAndTime.
         * @return msgCreationDateAndTime
         */
        public long getMsgCreationDateAndTime() {
            return msgCreationDateAndTime;
        }

        /**
         * Imposta il valore della proprietà msgCreationDateAndTime.
         * @param value value
         */
        public void setMsgCreationDateAndTime(long value) {
            this.msgCreationDateAndTime = value;
        }

        /**
         * Recupera il valore della proprietà msgId.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMsgId() {
            return msgId;
        }

        /**
         * Imposta il valore della proprietà msgId.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMsgId(String value) {
            this.msgId = value;
        }

        /**
         * Recupera il valore della proprietà caseId.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCaseId() {
            return caseId;
        }

        /**
         * Imposta il valore della proprietà caseId.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCaseId(String value) {
            this.caseId = value;
        }

        /**
         * Recupera il valore della proprietà msgHashCode.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMsgHashCode() {
            return msgHashCode;
        }

        /**
         * Imposta il valore della proprietà msgHashCode.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMsgHashCode(String value) {
            this.msgHashCode = value;
        }


        /**
         * <p>Classe Java per anonymous complex type.
         *
         * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
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
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "identifier",
                "contactInformation"
        })
        public static class Receiver {

            @XmlElement(name = "Identifier", required = true)
            protected String identifier;
            @XmlElement(name = "contactInformation", required = true)
            protected StandardBusinessDocumentHeader.Transport.Receiver.ContactInformation contactInformation;

            /**
             * Recupera il valore della proprietà identifier.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getIdentifier() {
                return identifier;
            }

            /**
             * Imposta il valore della proprietà identifier.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setIdentifier(String value) {
                this.identifier = value;
            }

            /**
             * Recupera il valore della proprietà contactInformation.
             *
             * @return
             *     possible object is
             *     {@link StandardBusinessDocumentHeader.Transport.Receiver.ContactInformation }
             *
             */
            public StandardBusinessDocumentHeader.Transport.Receiver.ContactInformation getContactInformation() {
                return contactInformation;
            }

            /**
             * Imposta il valore della proprietà contactInformation.
             *
             * @param value
             *     allowed object is
             *     {@link StandardBusinessDocumentHeader.Transport.Receiver.ContactInformation }
             *
             */
            public void setContactInformation(StandardBusinessDocumentHeader.Transport.Receiver.ContactInformation value) {
                this.contactInformation = value;
            }


            /**
             * <p>Classe Java per anonymous complex type.
             *
             * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
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
             *
             *
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                    "contact",
                    "emailAddress",
                    "faxNumber",
                    "telephoneNumber",
                    "contactTypeIdentifier"
            })
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

                /**
                 * Recupera il valore della proprietà contact.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getContact() {
                    return contact;
                }

                /**
                 * Imposta il valore della proprietà contact.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setContact(String value) {
                    this.contact = value;
                }

                /**
                 * Recupera il valore della proprietà emailAddress.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getEmailAddress() {
                    return emailAddress;
                }

                /**
                 * Imposta il valore della proprietà emailAddress.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setEmailAddress(String value) {
                    this.emailAddress = value;
                }

                /**
                 * Recupera il valore della proprietà faxNumber.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getFaxNumber() {
                    return faxNumber;
                }

                /**
                 * Imposta il valore della proprietà faxNumber.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setFaxNumber(String value) {
                    this.faxNumber = value;
                }

                /**
                 * Recupera il valore della proprietà telephoneNumber.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getTelephoneNumber() {
                    return telephoneNumber;
                }

                /**
                 * Imposta il valore della proprietà telephoneNumber.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setTelephoneNumber(String value) {
                    this.telephoneNumber = value;
                }

                /**
                 * Recupera il valore della proprietà contactTypeIdentifier.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getContactTypeIdentifier() {
                    return contactTypeIdentifier;
                }

                /**
                 * Imposta il valore della proprietà contactTypeIdentifier.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setContactTypeIdentifier(String value) {
                    this.contactTypeIdentifier = value;
                }

            }

        }


        /**
         * <p>Classe Java per anonymous complex type.
         *
         * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
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
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "identifier",
                "contactInformation"
        })
        public static class Sender {

            @XmlElement(name = "Identifier", required = true)
            protected String identifier;
            @XmlElement(name = "contactInformation", required = true)
            protected StandardBusinessDocumentHeader.Transport.Sender.ContactInformation contactInformation;

            /**
             * Recupera il valore della proprietà identifier.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getIdentifier() {
                return identifier;
            }

            /**
             * Imposta il valore della proprietà identifier.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setIdentifier(String value) {
                this.identifier = value;
            }

            /**
             * Recupera il valore della proprietà contactInformation.
             *
             * @return
             *     possible object is
             *     {@link StandardBusinessDocumentHeader.Transport.Sender.ContactInformation }
             *
             */
            public StandardBusinessDocumentHeader.Transport.Sender.ContactInformation getContactInformation() {
                return contactInformation;
            }

            /**
             * Imposta il valore della proprietà contactInformation.
             *
             * @param value
             *     allowed object is
             *     {@link StandardBusinessDocumentHeader.Transport.Sender.ContactInformation }
             *
             */
            public void setContactInformation(StandardBusinessDocumentHeader.Transport.Sender.ContactInformation value) {
                this.contactInformation = value;
            }


            /**
             * <p>Classe Java per anonymous complex type.
             *
             * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
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
             *
             *
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                    "contact",
                    "emailAddress",
                    "faxNumber",
                    "telephoneNumber",
                    "contactTypeIdentifier"
            })
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

                /**
                 * Recupera il valore della proprietà contact.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getContact() {
                    return contact;
                }

                /**
                 * Imposta il valore della proprietà contact.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setContact(String value) {
                    this.contact = value;
                }

                /**
                 * Recupera il valore della proprietà emailAddress.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getEmailAddress() {
                    return emailAddress;
                }

                /**
                 * Imposta il valore della proprietà emailAddress.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setEmailAddress(String value) {
                    this.emailAddress = value;
                }

                /**
                 * Recupera il valore della proprietà faxNumber.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getFaxNumber() {
                    return faxNumber;
                }

                /**
                 * Imposta il valore della proprietà faxNumber.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setFaxNumber(String value) {
                    this.faxNumber = value;
                }

                /**
                 * Recupera il valore della proprietà telephoneNumber.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getTelephoneNumber() {
                    return telephoneNumber;
                }

                /**
                 * Imposta il valore della proprietà telephoneNumber.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setTelephoneNumber(String value) {
                    this.telephoneNumber = value;
                }

                /**
                 * Recupera il valore della proprietà contactTypeIdentifier.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getContactTypeIdentifier() {
                    return contactTypeIdentifier;
                }

                /**
                 * Imposta il valore della proprietà contactTypeIdentifier.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setContactTypeIdentifier(String value) {
                    this.contactTypeIdentifier = value;
                }

            }

        }

    }

}
