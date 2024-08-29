/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.scheduler.job;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.scheduler.configuration.DomibusConnectorClientSchedulerAutoConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
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

/**
 * The {@code GetMessagesFromConnectorJobConfiguration} class is a configuration class that defines
 * a job to get messages from the Domibus Connector and deliver them to the client backend. It
 * extends the {@code Job} interface, which is a marker interface for Quartz Jobs.
 */
@EnableConfigurationProperties
@ConditionalOnProperty(
    prefix = GetMessagesFromConnectorJobConfigurationProperties.PREFIX, value = "enabled",
    havingValue = "true"
)
@Configuration("getMessagesFromConnectorJobConfiguration")
@DisallowConcurrentExecution
public class GetMessagesFromConnectorJobConfiguration implements Job {
    private static final Logger LOGGER =
        LogManager.getLogger(GetMessagesFromConnectorJobConfiguration.class);
    @Autowired
    private GetMessagesFromConnectorJobService getMessagesFromConnectorJob;
    @Autowired
    GetMessagesFromConnectorJobConfigurationProperties properties;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.debug("Running GetMessagesFromConnectorJob");
        try {
            getMessagesFromConnectorJob
                .requestNewMessagesFromConnectorAndDeliverThemToClientBackend();
        } catch (DomibusConnectorClientException e) {
            throw new JobExecutionException(e);
        }
    }

    @Bean(name = "getMessagesFromConnectorJob")
    public JobDetailFactoryBean getMessagesFromConnectorJob() {
        return DomibusConnectorClientSchedulerAutoConfiguration.createJobDetail(this.getClass());
    }

    /**
     * Returns a {@code SimpleTriggerFactoryBean} object that is configured with the specified
     * parameters. The trigger is created for the job specified by the provided
     * {@code JobDetailFactoryBean}. The trigger has a start delay, a repeat interval, and a misfire
     * instruction set.
     *
     * @param jobDetailFactoryBean the {@code JobDetailFactoryBean} object representing the job to
     *                             be triggered
     * @return the created {@code SimpleTriggerFactoryBean} object
     * @see JobDetailFactoryBean
     * @see SimpleTriggerFactoryBean
     */
    @Bean(name = "getMessagesFromConnectorTrigger")
    public SimpleTriggerFactoryBean getMessagesFromConnectorTrigger(
        @Qualifier("getMessagesFromConnectorJob") JobDetailFactoryBean jobDetailFactoryBean) {
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
