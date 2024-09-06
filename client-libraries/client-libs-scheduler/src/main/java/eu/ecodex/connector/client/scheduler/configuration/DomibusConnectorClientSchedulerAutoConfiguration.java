/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.scheduler.configuration;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.util.CollectionUtils;

/**
 * Configuration class for the Domibus Connector Client Scheduler. This class creates and configures
 * a SchedulerFactoryBean instance, sets the job factory, triggers, and properties for the
 * scheduler. It also provides utility methods to create job details and triggers with the specified
 * parameters.
 */
@Configuration
@PropertySource("classpath:connector-client-quartz.properties") // load default quartz properties
public class DomibusConnectorClientSchedulerAutoConfiguration {
    @Autowired(required = false)
    List<Trigger> listOfTrigger;

    /**
     * Creates a JobFactory object that sets the ApplicationContext for the
     * AutowiringSpringBeanJobFactory and returns the created JobFactory object.
     *
     * @param applicationContext the ApplicationContext object to be set for the
     *                           AutowiringSpringBeanJobFactory
     * @return the created JobFactory object with the ApplicationContext set
     */
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        var jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /**
     * Creates and configures an instance of SchedulerFactoryBean with the specified JobFactory.
     *
     * @param jobFactory the JobFactory to be set for the SchedulerFactoryBean
     * @return the configured SchedulerFactoryBean instance
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) {
        var factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true);
        factory.setJobFactory(jobFactory);

        // Here we will set all the trigger beans we have defined.
        if (!CollectionUtils.isEmpty(listOfTrigger)) {
            factory.setTriggers(listOfTrigger.toArray(new Trigger[listOfTrigger.size()]));
        }

        return factory;
    }

    /**
     * Retrieves the Quartz properties for configuring the Quartz scheduler. This method reads the
     * Quartz properties from the "connector-client-quartz.properties" file located in the
     * classpath. It returns a Properties object containing the Quartz properties.
     *
     * @return the Quartz properties as a Properties object
     * @throws IOException if there is an error reading the properties file
     */
    @Bean
    public Properties quartzProperties() throws IOException {
        var propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(
            new ClassPathResource("/connector-client-quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    /**
     * Creates a SimpleTriggerFactoryBean object with the specified parameters. The
     * SimpleTriggerFactoryBean is configured with the given jobDetail, pollFrequencyMs, and
     * startDelay. It sets the jobDetail, startDelay, repeatInterval, repeatCount, and
     * misfireInstruction properties of the factoryBean.
     *
     * @param jobDetail       the JobDetail object to be set for the factoryBean
     * @param pollFrequencyMs the poll frequency in milliseconds
     * @param startDelay      the start delay in milliseconds
     * @return the created SimpleTriggerFactoryBean object
     */
    public static SimpleTriggerFactoryBean createTrigger(
        JobDetail jobDetail, long pollFrequencyMs, long startDelay) {
        var factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setStartDelay(startDelay);
        factoryBean.setRepeatInterval(pollFrequencyMs);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        // in case of misfire, ignore all missed triggers and continue :
        factoryBean.setMisfireInstruction(
            SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
        return factoryBean;
    }

    /**
     * Creates a JobDetailFactoryBean object that sets the job class and returns it.
     *
     * @param jobClass the class that represents the job to be created
     * @return the created JobDetailFactoryBean object
     */
    public static JobDetailFactoryBean createJobDetail(
        @SuppressWarnings("rawtypes") Class jobClass) {
        var factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        // job has to be durable to be stored in DB:
        // factoryBean.setDurability(true);
        return factoryBean;
    }
}
