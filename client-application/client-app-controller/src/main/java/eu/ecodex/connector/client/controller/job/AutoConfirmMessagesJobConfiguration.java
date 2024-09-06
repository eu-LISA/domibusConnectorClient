/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.controller.job;

import eu.ecodex.connector.client.scheduler.configuration.DomibusConnectorClientSchedulerAutoConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * Configuration class for the AutoConfirmMessagesJob. This class is responsible for configuring the
 * job execution and triggering the auto confirmation of messages.
 */
@EnableConfigurationProperties
@ConditionalOnProperty(
    prefix = AutoConfirmMessagesJobConfigurationProperties.PREFIX, value = "enabled",
    havingValue = "true"
)
@Configuration("autoConfirmMessagesJobConfiguration")
public class AutoConfirmMessagesJobConfiguration implements Job {
    private static final Logger LOGGER =
        LogManager.getLogger(AutoConfirmMessagesJobConfiguration.class);
    @Autowired
    private AutoConfirmMessagesJobService autoConfirmMessagesService;
    @Autowired
    AutoConfirmMessagesJobConfigurationProperties properties;

    @Override
    public void execute(JobExecutionContext context) {
        LOGGER.debug("Running AutoConfirmMessagesJob");
        autoConfirmMessagesService.autoConfirmMessages();
    }

    @Bean(name = "autoConfirmMessagesJob")
    public JobDetailFactoryBean autoConfirmMessagesJob() {
        return DomibusConnectorClientSchedulerAutoConfiguration.createJobDetail(this.getClass());
    }

    /**
     * Creates a SimpleTriggerFactoryBean object to configure the trigger for the
     * autoConfirmMessagesJob.
     *
     * @param jobDetailFactoryBean the JobDetailFactoryBean for the autoConfirmMessagesJob
     * @return the SimpleTriggerFactoryBean for the trigger
     */
    @Bean(name = "autoConfirmMessagesTrigger")
    public SimpleTriggerFactoryBean autoConfirmMessagesTrigger(
        @Qualifier("autoConfirmMessagesJob") JobDetailFactoryBean jobDetailFactoryBean) {
        if (!properties.isEnabled()) {
            return null;
        }
        return DomibusConnectorClientSchedulerAutoConfiguration.createTrigger(
            jobDetailFactoryBean.getObject(),
            properties.getRepeatInterval().getMilliseconds(),
            properties.getRepeatInterval().getMilliseconds() / 2
        );
    }
}
