/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.scheduler.job;

import eu.ecodex.connector.client.scheduler.configuration.DomibusConnectorClientSchedulerAutoConfiguration;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * This class represents the job configuration for submitting messages to the connector. It is
 * responsible for setting up the job and trigger, as well as executing the job.
 *
 * @see SubmitMessagesToConnectorJobService
 * @see SubmitMessagesToConnectorJobConfigurationProperties
 * @see Job
 */
@EnableConfigurationProperties
@Configuration("submitMessagesToConnectorJobConfiguration")
@ConditionalOnProperty(
    value = SubmitMessagesToConnectorJobConfigurationProperties.PREFIX + ".enabled",
    havingValue = "true"
)
@DisallowConcurrentExecution
public class SubmitMessagesToConnectorJobConfiguration implements Job {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(SubmitMessagesToConnectorJobConfiguration.class);
    @Autowired
    SubmitMessagesToConnectorJobService submitMessagesToConnectorJob;
    @Autowired
    SubmitMessagesToConnectorJobConfigurationProperties properties;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.debug("Running SubmitMessagesToConnectorJob");
        submitMessagesToConnectorJob.checkClientBackendForNewMessagesAndSubmitThemToConnector();
    }

    @Bean(name = "submitMessagesToConnectorJob")
    public JobDetailFactoryBean submitMessagesToConnectorJob() {
        return DomibusConnectorClientSchedulerAutoConfiguration.createJobDetail(this.getClass());
    }

    /**
     * Creates a SimpleTriggerFactoryBean object with the specified parameters. The
     * SimpleTriggerFactoryBean is configured with the given jobDetail, pollFrequencyMs, and
     * startDelay. It sets the jobDetail, startDelay, repeatInterval, repeatCount, and
     * misfireInstruction properties of the factoryBean.
     *
     * @param jobDetailFactoryBean the JobDetailFactoryBean object used to create the JobDetail
     *                             instance for the trigger
     * @return the created SimpleTriggerFactoryBean object
     */
    @Bean(name = "submitMessagesToConnectorTrigger")
    public SimpleTriggerFactoryBean submitMessagesToConnectorTrigger(
        @Qualifier("submitMessagesToConnectorJob") JobDetailFactoryBean jobDetailFactoryBean) {
        LOGGER.debug("create SimpleTriggerFactoryBean: submitMessagesToConnectorTrigger");
        return DomibusConnectorClientSchedulerAutoConfiguration.createTrigger(
            jobDetailFactoryBean.getObject(),
            properties.getRepeatInterval().getMilliseconds(),
            0L
        );
    }
}
