package eu.domibus.connector.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.SystemPropertyUtils;

@SpringBootApplication(scanBasePackages = "eu.domibus.connector.client")
@EnableScheduling
@EnableTransactionManagement
@PropertySource({"classpath:/default-connector-client.properties"})
public class VaadinConnectorClientStarter {

private static final Logger LOGGER = LoggerFactory.getLogger(VaadinConnectorClientStarter.class);

    public static final String CONNECTOR_CLIENT_CONFIG_FILE = "connector-client.properties";

    public static final String SPRING_CONFIG_LOCATION_PROPERTY_NAME = "spring.config.location";
    public static final String SPRING_CONFIG_NAME_PROPERTY_NAME = "spring.config.name";
    public static final String SPRING_CONFIG_NAME = "connector-client";
	
   
	
	public static void main(String[] args) {
		runSpringApplication(args);

	}
	
	public static ConfigurableApplicationContext runSpringApplication(String[] args) {
    	SpringApplicationBuilder builder = new SpringApplicationBuilder();
        builder = configureApplicationContext(builder);
    	SpringApplication springApplication = builder.build();
        ConfigurableApplicationContext appContext = springApplication.run(args);
        return appContext;
    }
	
	public static SpringApplicationBuilder configureApplicationContext(SpringApplicationBuilder application) {
        String connectorConfigFile = getConnectorConfigFile();
        Properties springProperties = new Properties();
        if (connectorConfigFile != null) {

            int lastIndex = connectorConfigFile.contains(File.separator)?connectorConfigFile.lastIndexOf(File.separatorChar):connectorConfigFile.lastIndexOf("/");
            lastIndex++;
//            String connectorConfigLocation = connectorConfigFile.substring(0, lastIndex);
            String configName = connectorConfigFile.substring(lastIndex);

            LOGGER.info(String.format("Setting:\n%s=%s\n%s=%s",
                    SPRING_CONFIG_LOCATION_PROPERTY_NAME, connectorConfigFile,
                    SPRING_CONFIG_NAME_PROPERTY_NAME, configName));

            springProperties.setProperty(SPRING_CONFIG_LOCATION_PROPERTY_NAME, connectorConfigFile);
            springProperties.setProperty(SPRING_CONFIG_NAME_PROPERTY_NAME, configName);

        }else {
            LOGGER.warn("SystemProperty \"{}\" not given or not resolveable! Startup using default spring external configuration!", CONNECTOR_CLIENT_CONFIG_FILE);
			springProperties.setProperty(SPRING_CONFIG_NAME_PROPERTY_NAME, SPRING_CONFIG_NAME); //look for <SPRING_CONFIG_NAME>.properties in config location
        }
        application.properties(springProperties); //pass the mapped CONNECTOR_CONFIG_FILE to the spring properties...
        return application.sources(VaadinConnectorClientStarter.class);
    }
	
	public static @Nullable
    String getConnectorConfigFile() {
        String connectorConfigFile = System.getProperty(CONNECTOR_CLIENT_CONFIG_FILE);
        if (connectorConfigFile != null) {
            connectorConfigFile = SystemPropertyUtils.resolvePlaceholders(connectorConfigFile);
            return connectorConfigFile;
        }
        return null;
    }
	
//	 @Override
//	    public void onStartup(ServletContext servletContext) throws ServletException {
//	        this.servletContext = servletContext;
//
//	        //read logging.config from connector properties and set it before the application context ist started
//	        //so its already available for the spring logging servlet initializer to configure logging!
//	        String connectorConfigFile = getConnectorConfigFile();
//	        if (connectorConfigFile != null) {
//	            Properties p = loadConnectorConfigProperties(connectorConfigFile);
//	            String loggingConfig = p.getProperty("logging.config");
//	            if (loggingConfig != null) {
//	                servletContext.setInitParameter("logging.config", loggingConfig);
//	            }
//	        }
//	        super.onStartup(servletContext);
//	    }
	 
//	 public static Properties loadConnectorConfigProperties(String connectorConfigFile) {
//	        Properties p = new Properties();
//	        if (connectorConfigFile != null) {
//	            java.nio.file.Path connectorConfigFilePath = Paths.get(connectorConfigFile);
//	            if (!Files.exists(connectorConfigFilePath)) {
//	                String errorString = String.format("Cannot start because the via System Property [%s] provided config file [%s] mapped to path [%s] does not exist!", CONNECTOR_CLIENT_CONFIG_FILE, connectorConfigFile, connectorConfigFilePath);
//	                LOGGER.error(errorString);
//	                throw new RuntimeException(errorString);
//	            }
//	            try {
//
//	                FileInputStream fileInputStream = new FileInputStream(connectorConfigFilePath.toFile());
//					p.load(fileInputStream);
//					fileInputStream.close();
//	                return p;
//	            } catch (IOException e) {
//	                throw new RuntimeException(String.format("Cannot load properties from file [%s], is it a valid and readable properties file?", connectorConfigFilePath), e);
//	            }
//	        }
//	        return p;
//	    }

}
