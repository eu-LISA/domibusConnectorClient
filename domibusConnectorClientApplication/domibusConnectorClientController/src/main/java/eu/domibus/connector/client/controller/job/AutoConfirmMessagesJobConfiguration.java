package eu.domibus.connector.client.controller.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import eu.domibus.connector.client.scheduler.configuration.DomibusConnectorClientSchedulerAutoConfiguration;

@EnableConfigurationProperties
@ConditionalOnProperty(prefix = AutoConfirmMessagesJobConfigurationProperties.PREFIX, value = "enabled", havingValue = "true")
@Configuration("autoConfirmMessagesJobConfiguration")
public class AutoConfirmMessagesJobConfiguration implements Job {
	
	private static final Logger LOGGER = LogManager.getLogger(AutoConfirmMessagesJobConfiguration.class);

	@Autowired
	private AutoConfirmMessagesJobService autoConfirmMessagesService;

	@Autowired
	AutoConfirmMessagesJobConfigurationProperties properties;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.debug("Running AutoConfirmMessagesJob");
//        try {
		autoConfirmMessagesService.autoConfirmMessages();
//        } catch (DomibusConnectorClientException e) {
//            throw new JobExecutionException(e);
//        }
	}

	@Bean(name = "autoConfirmMessagesJob")
	public JobDetailFactoryBean autoConfirmMessagesJob() {
		return DomibusConnectorClientSchedulerAutoConfiguration.createJobDetail(this.getClass());
	}

	@Bean(name = "autoConfirmMessagesTrigger")
	public SimpleTriggerFactoryBean autoConfirmMessagesTrigger(@Qualifier("autoConfirmMessagesJob") JobDetailFactoryBean jdfb ) {
		if (!properties.isEnabled())
			return null;
		return DomibusConnectorClientSchedulerAutoConfiguration.createTrigger(jdfb.getObject(),
				properties.getRepeatInterval().getMilliseconds(),
				properties.getRepeatInterval().getMilliseconds()/2);
	}

}
