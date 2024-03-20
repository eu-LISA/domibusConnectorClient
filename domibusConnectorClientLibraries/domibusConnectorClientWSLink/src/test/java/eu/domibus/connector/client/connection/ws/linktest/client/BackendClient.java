
package eu.domibus.connector.client.connection.ws.linktest.client;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@SpringBootApplication(scanBasePackages = "eu.domibus.connector.client.connection")
@Configuration
//@Import(DomibusConnectorClientWebServiceLinkConfiguration.class)
public class BackendClient {

    
    public static ApplicationContext startSpringApplication(String[] profiles, String[] properties) {

        //TODO: edit for push test
        boolean web;
        List<String> p = Arrays.asList(profiles);
        web = p.contains("push"); //if profile is push start webserver for push interface
                    
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        SpringApplication springApp = builder.bannerMode(Banner.Mode.OFF)
                .sources(BackendClient.class)
                .properties(properties)
                .profiles(profiles)
                .web(WebApplicationType.SERVLET)
                .build();

        ConfigurableApplicationContext appContext = springApp.run();
        
        return appContext;
    }
    
    
}
