package eu.domibus.connector.client.controller.configuration;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import eu.domibus.connector.client.controller.database.DatabaseRestore;
import eu.domibus.connector.client.controller.persistence.dao.PackageDomibusConnectorClientRepositories;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientPersistenceModel;
import eu.domibus.connector.client.controller.persistence.service.DomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;

@Configuration
@EntityScan(basePackageClasses={PDomibusConnectorClientPersistenceModel.class})
@EnableJpaRepositories(basePackageClasses = {PackageDomibusConnectorClientRepositories.class} )
@EnableTransactionManagement
@ConfigurationProperties(prefix = DomibusConnectorClientControllerConfig.PREFIX)
@PropertySource("classpath:/connector-client-controller-default.properties")
public class DomibusConnectorClientControllerConfig {
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientControllerConfig.class);

	public static final String PREFIX = "connector-client.controller";
	public static final String RESTORE_DATABASE_PROPERTY = "restore-database-from-storage";
	
	@Autowired
	private DomibusConnectorClientStorage storage;
	
	@Autowired
	private DomibusConnectorClientPersistenceService persistenceService;
	
	@NestedConfigurationProperty
    @NotNull
    private DefaultConfirmationAction confirmationDefaultAction;
	
	public DomibusConnectorClientControllerConfig() {
		// TODO Auto-generated constructor stub
	}

	public DefaultConfirmationAction getConfirmationDefaultAction() {
		return confirmationDefaultAction;
	}

	public void setConfirmationDefaultAction(DefaultConfirmationAction confirmationDefaultAction) {
		this.confirmationDefaultAction = confirmationDefaultAction;
	}
	
	@Bean
	@ConditionalOnProperty(prefix = DomibusConnectorClientControllerConfig.PREFIX, value = RESTORE_DATABASE_PROPERTY, havingValue="true", matchIfMissing = false)
	public void restoreDatabaseFromStorage() {
		LOGGER.debug("#restoreDatabaseFromStorage: enter");
		
		DatabaseRestore restore = new DatabaseRestore();
		
		restore.setPersistenceService(persistenceService);
		restore.setStorage(storage);
		
		restore.restoreDatabaseFromStorage();
		
	}

}
