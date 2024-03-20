
package eu.domibus.connector.client.spring;

import javax.validation.Valid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.schema.validation.SeverityLevel;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Configuration
@ConfigurationProperties(prefix = ConnectorClientAutoConfiguration.PREFIX)
@PropertySource("classpath:/connector-client-library-default.properties")
@Validated
@Valid
public class ConnectorClientAutoConfiguration {

	public SeverityLevel getSchemaValidationMaxSeverityLevel() {
		return schemaValidationMaxSeverityLevel;
	}

	public void setSchemaValidationMaxSeverityLevel(SeverityLevel schemaValidationMaxSeverityLevel) {
		this.schemaValidationMaxSeverityLevel = schemaValidationMaxSeverityLevel;
	}

	public static final String PREFIX = "connector-client.library";
	
	@Nullable
    private SeverityLevel schemaValidationMaxSeverityLevel;

//    /**
//     * if not available create a default ContentMapper Bean.
//     * This implementation does a identity mapping so the business
//     * xml is not changed
//     * @return - a 1:1 mapping implementation
//     */
//    @Bean
//    @ConditionalOnMissingBean(DomibusConnectorClientContentMapper.class)
//    public DomibusConnectorClientContentMapper contentMapper() {
//    	return new DomibusConnectorClientContentMapperDefaultImpl();
//    }

	private boolean acknowledgeMessagesRequired=false;
	
	@Nullable
	private Integer requestMessagesMaxCount;



	public boolean isAcknowledgeMessagesRequired() {
		return acknowledgeMessagesRequired;
	}

	public void setAcknowledgeMessagesRequired(boolean acknowledgeMessagesRequired) {
		this.acknowledgeMessagesRequired = acknowledgeMessagesRequired;
	}

	public Integer getRequestMessagesMaxCount() {
		return requestMessagesMaxCount;
	}

	public void setRequestMessagesMaxCount(Integer requestMessagesMaxCount) {
		this.requestMessagesMaxCount = requestMessagesMaxCount;
	}


	
}
