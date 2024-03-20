package eu.domibus.connector.client.war;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.SystemPropertyUtils;

@SpringBootApplication(scanBasePackages = "eu.domibus.connector.client")
@EnableScheduling
@EnableTransactionManagement
@PropertySource({"classpath:/default-connector-client.properties"})
public class DomibusConnectorClientWAR extends SpringBootServletInitializer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorClientWAR.class);
	
	public static final String SPRING_CONFIG_LOCATION_PROPERTY_NAME = "spring.config.location";
    public static final String SPRING_CONFIG_NAME_PROPERTY_NAME = "spring.config.name";
	
	public static final String CONNECTOR_CONFIG_FILE_PROPERTY_NAME = "connector-client.config.file";
    
    public static final String DEFAULT_SPRING_CONFIG_NAME = "connector-client";
    public static final String DEFAULT_SPRING_CONFIG_LOCATION = "classpath:/config/,file:./conf/,file:./conf/connector-client/,file:./config/,file:./config/connector-client/";

    
    String connectorConfigFile = null;

    String springConfigLocation = DEFAULT_SPRING_CONFIG_LOCATION;
    String springConfigName = DEFAULT_SPRING_CONFIG_NAME;

    Properties springApplicationProperties = new Properties();
	
	public SpringApplicationBuilder configureApplicationContext(SpringApplicationBuilder application) {
        
		if(connectorConfigFile==null) {
			//connectorConfigFile not initialized by servletContext init parameter...
			//try to read as system parameter
			connectorConfigFile = getConnectorConfigFileSystemProperty();
		}

		if (connectorConfigFile != null) {

            int lastIndex = connectorConfigFile.contains(File.separator)?connectorConfigFile.lastIndexOf(File.separatorChar):connectorConfigFile.lastIndexOf("/");
            lastIndex++;
            String configName = connectorConfigFile.substring(lastIndex);

            LOGGER.info(String.format("Setting:\n%s=%s\n%s=%s",
            		SPRING_CONFIG_LOCATION_PROPERTY_NAME, connectorConfigFile,
            		SPRING_CONFIG_NAME_PROPERTY_NAME, configName));

            springApplicationProperties.setProperty(SPRING_CONFIG_LOCATION_PROPERTY_NAME, connectorConfigFile);
            springApplicationProperties.setProperty(SPRING_CONFIG_NAME_PROPERTY_NAME, configName);

        }else {
        	springApplicationProperties.setProperty(SPRING_CONFIG_LOCATION_PROPERTY_NAME, springConfigLocation);
            springApplicationProperties.setProperty(SPRING_CONFIG_NAME_PROPERTY_NAME, springConfigName);
            LOGGER.warn("No \"{}\" parameter given or resolveable! Startup using default spring external configuration.", CONNECTOR_CONFIG_FILE_PROPERTY_NAME);
        }
		LOGGER.info(String.format("Setting:\n%s=%s\n%s=%s",
        		SPRING_CONFIG_LOCATION_PROPERTY_NAME, springApplicationProperties.get(SPRING_CONFIG_LOCATION_PROPERTY_NAME),
        		SPRING_CONFIG_NAME_PROPERTY_NAME, springApplicationProperties.get(SPRING_CONFIG_NAME_PROPERTY_NAME)));
        application.properties(springApplicationProperties); //pass the mapped CONNECTOR_CONFIG_FILE to the spring properties...
        return application.sources(DomibusConnectorClientWAR.class);
    }
	
	public static @Nullable
    String getConnectorConfigFileSystemProperty() {
        String connectorConfigFile = System.getProperty(CONNECTOR_CONFIG_FILE_PROPERTY_NAME);
        if (connectorConfigFile != null) {
            connectorConfigFile = SystemPropertyUtils.resolvePlaceholders(connectorConfigFile);
            return connectorConfigFile;
        }
        return null;
    }

    
    /***
     * {@inheritDoc}
     *
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return configureApplicationContext(application);
    }
    
	 public static Properties loadConnectorConfigProperties(String connectorConfigFile) {
	        Properties p = new Properties();
	        if (connectorConfigFile != null) {
	            Path connectorConfigFilePath = Paths.get(connectorConfigFile);
	            if (!Files.exists(connectorConfigFilePath)) {
	                String errorString = String.format("Cannot start because the via System Property [%s] provided config file [%s] mapped to path [%s] does not exist!", CONNECTOR_CONFIG_FILE_PROPERTY_NAME, connectorConfigFile, connectorConfigFilePath);
	                LOGGER.error(errorString);
	                throw new RuntimeException(errorString);
	            }
	            try {
	            	
	                FileInputStream fileInputStream = new FileInputStream(connectorConfigFilePath.toFile());
					p.load(fileInputStream);
					fileInputStream.close();
	                return p;
	            } catch (IOException e) {
	                throw new RuntimeException(String.format("Cannot load properties from file [%s], is it a valid and readable properties file?", connectorConfigFilePath), e);
	            }
	        }
	        return p;
	    }
	 
	 /**
	     * Will only be called if the Application is deployed within an web application server
	     * adds to the boostrap and spring config location search path a web application context
	     * dependent search path:
	     *  app deployed under context /connector will look also for config under [workingpath]/config/[webcontext]/,
	     *  [workingpath]/conf/[webcontext]/
	     *
	     * @param servletContext the servlet context
	     * @throws ServletException in case of an error @see {@link SpringBootServletInitializer#onStartup(ServletContext)} 
	     *
	     * {@inheritDoc}
	     *
	     */
	    @Override
	    public void onStartup(ServletContext servletContext) throws ServletException {
	        if (servletContext != null) {

	        	connectorConfigFile = servletContext.getInitParameter(CONNECTOR_CONFIG_FILE_PROPERTY_NAME);
	        	
	        	if (connectorConfigFile == null) {
	        		// if connector config file not a parameter in servlet context, set the servlet context path as spring config
	        		springConfigLocation = springConfigLocation +
	        				",file:./config/" + servletContext.getContextPath() + "/" +
	        				",file:./conf/" + servletContext.getContextPath() + "/";

	        		springConfigName = servletContext.getContextPath();
	        	}
	        	
	        	
	        	super.onStartup(servletContext);
	        }
	        
	    }

}
