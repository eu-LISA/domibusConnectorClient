CREATE TABLE IF NOT EXISTS CONNECTOR_CLIENT_MESSAGE (
    ID INT AUTO_INCREMENT  PRIMARY KEY,
    EBMS_MESSAGE_ID VARCHAR(255) UNIQUE,
    BACKEND_MESSAGE_ID VARCHAR(255) UNIQUE,
    CONVERSATION_ID VARCHAR(255),
    ORIGINAL_SENDER VARCHAR(255),
    FINAL_RECIPIENT VARCHAR(255),
    FROM_PARTY_ID VARCHAR(255),
    FROM_PARTY_TYPE VARCHAR(255),
    FROM_PARTY_ROLE VARCHAR(255),
    TO_PARTY_ID VARCHAR(255),
    TO_PARTY_TYPE VARCHAR(255),
    TO_PARTY_ROLE VARCHAR(255),
    SERVICE VARCHAR(255),
    SERVICE_TYPE VARCHAR(512),
    ACTION VARCHAR(255),
    STORAGE_STATUS VARCHAR(255),
    STORAGE_INFO VARCHAR(255),
    MESSAGE_STATUS VARCHAR(255),
    CREATED TIMESTAMP
);

CREATE TABLE IF NOT EXISTS CONNECTOR_CLIENT_CONFIRMATION (
    ID INT AUTO_INCREMENT  PRIMARY KEY,
    MESSAGE_ID INT NOT NULL,
    CONFIRMATION_TYPE VARCHAR(255) NOT NULL,
    RECEIVED TIMESTAMP,
    foreign key (MESSAGE_ID) references CONNECTOR_CLIENT_MESSAGE(ID)
);

CREATE SEQUENCE IF NOT EXISTS CLIENT_MESSAGE_SEQ start with 100 increment by 1;

CREATE SEQUENCE IF NOT EXISTS CLIENT_CONFIRMATION_SEQ start with 100 increment by 1;
