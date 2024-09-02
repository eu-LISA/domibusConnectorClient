/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.controller.job;

import eu.domibus.connector.client.scheduler.configuration.DomibusConnectorClientSchedulerAutoConfiguration;
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
 * The {@code UpdateStorageStatusJobConfiguration} class is a configuration class for the Update
 * Storage Status Job in the Domibus Connector Client module. It implements the Job interface from
 * the Quartz library. This class is responsible for configuring the job, providing necessary
 * dependencies, and defining the job execution logic.
 */
@EnableConfigurationProperties
@ConditionalOnProperty(
    prefix = UpdateStorageStatusJobConfigurationProperties.PREFIX, value = "enabled",
    havingValue = "true"
)
@Configuration("updateStorageStatusJobConfiguration")
public class UpdateStorageStatusJobConfiguration implements Job {
    private static final Logger LOGGER =
        LogManager.getLogger(UpdateStorageStatusJobConfiguration.class);
    @Autowired
    private UpdateStorageStatusJobService updateStorageStatusService;
    @Autowired
    UpdateStorageStatusJobConfigurationProperties properties;

    @Override
    public void execute(JobExecutionContext context) {
        LOGGER.debug("Running UpdateStorageStatusJob");
        updateStorageStatusService.checkStorageAndUpdateDatabaseMessages();
    }

    @Bean(name = "updateStorageStatusJob")
    public JobDetailFactoryBean updateStorageStatusJob() {
        return DomibusConnectorClientSchedulerAutoConfiguration.createJobDetail(this.getClass());
    }

    /**
     * Creates a trigger for updating the storage status.
     *
     * @param jobDetailFactoryBean The JobDetailFactoryBean for the job that updates the storage
     *                             status.
     * @return The SimpleTriggerFactoryBean for the trigger, or null if the feature is disabled.
     */
    @Bean(name = "updateStorageStatusTrigger")
    public SimpleTriggerFactoryBean updateStorageStatusTrigger(
        @Qualifier("updateStorageStatusJob") JobDetailFactoryBean jobDetailFactoryBean) {
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
